package org.training.iso;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Minimal ISO 8583 host for the WSO2 MI training labs.
 *
 * Works with the WSO2 ISO8583 connector: the connector writes one packed message (no length prefix)
 * and reads the reply until the socket is closed. This server unpacks the request with the same
 * jposdef.xml the connector uses, replies with MTI + 10 (e.g. 0200 -> 0210), field 39 = "00"
 * (approved) and an authorization code in field 38, then closes the connection.
 *
 * Usage: java -jar ISO8583TestServer.jar [port]   (default port 5010)
 */
public class ISO8583TestServer {

    private static final AtomicInteger AUTH_CODE = new AtomicInteger(100000);

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5010;
        GenericPackager packager = new GenericPackager(
                ISO8583TestServer.class.getClassLoader().getResourceAsStream("jposdef.xml"));

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("ISO 8583 test server listening on port " + port + " (Ctrl+C to stop)");
            while (true) {
                try (Socket socket = server.accept()) {
                    handle(socket, packager);
                } catch (Throwable e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private static void handle(Socket socket, GenericPackager packager) throws Exception {
        byte[] request = readMessage(socket);
        if (request.length == 0) {
            return;
        }
        System.out.println("\n<< Request (" + request.length + " bytes): " + new String(request, StandardCharsets.US_ASCII));

        ISOMsg in = new ISOMsg();
        in.setPackager(packager);
        in.unpack(request);
        print("<< Request fields", in);

        ISOMsg out = (ISOMsg) in.clone();
        out.setPackager(packager);
        out.setResponseMTI();
        out.set(38, String.valueOf(AUTH_CODE.incrementAndGet()));
        out.set(39, "00");
        byte[] response = out.pack();
        print(">> Response fields", out);

        OutputStream os = socket.getOutputStream();
        os.write(response);
        os.flush();
        System.out.println(">> Response sent (" + response.length + " bytes): "
                + new String(response, StandardCharsets.US_ASCII));
    }

    // The connector sends one message without a length prefix, so read until no more data arrives.
    private static byte[] readMessage(Socket socket) throws Exception {
        InputStream is = socket.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        socket.setSoTimeout(10000);
        int n = is.read(chunk);
        if (n <= 0) {
            return new byte[0];
        }
        buffer.write(chunk, 0, n);
        socket.setSoTimeout(300);
        try {
            while ((n = is.read(chunk)) > 0) {
                buffer.write(chunk, 0, n);
            }
        } catch (SocketTimeoutException ignored) {
            // no more data for this message
        }
        return buffer.toByteArray();
    }

    private static void print(String title, ISOMsg msg) {
        System.out.println(title + ":");
        for (int i = 0; i <= msg.getMaxField(); i++) {
            if (msg.hasField(i)) {
                System.out.println("   field " + i + " = " + msg.getString(i));
            }
        }
    }
}
