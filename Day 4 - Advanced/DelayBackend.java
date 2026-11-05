import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lab backend with a delay you can change while it runs.
 *
 *   GET  /orders                 answers {"orders":[...]} after the current delay
 *   POST /admin/delay?ms=8000    sets the delay for all following requests (0 = no delay)
 *   GET  /admin/delay            shows the current delay and the number of requests so far
 *
 * Start:  java DelayBackend.java [port] [initialDelayMs]     (JDK 11 or newer, default port 9090)
 */
public class DelayBackend {

    private static final AtomicLong delayMs = new AtomicLong(0);
    private static final AtomicInteger counter = new AtomicInteger();

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 9090;
        if (args.length > 1) {
            delayMs.set(Long.parseLong(args[1]));
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());   // slow requests must not block each other

        server.createContext("/orders", ex -> {
            int n = counter.incrementAndGet();
            long d = delayMs.get();
            System.out.println("#" + n + " " + ex.getRequestMethod() + " /orders  waiting " + d + " ms");
            sleep(d);
            reply(ex, 200, "{\"orders\":[{\"id\":1,\"item\":\"book\"},{\"id\":2,\"item\":\"pen\"}],\"request\":" + n + "}");
            System.out.println("#" + n + " answered");
        });

        server.createContext("/admin/delay", ex -> {
            String query = ex.getRequestURI().getQuery();
            if ("POST".equals(ex.getRequestMethod()) && query != null && query.startsWith("ms=")) {
                delayMs.set(Long.parseLong(query.substring(3)));
                System.out.println("delay is now " + delayMs.get() + " ms");
            }
            reply(ex, 200, "{\"delayMs\":" + delayMs.get() + ",\"requests\":" + counter.get() + "}");
        });

        server.start();
        System.out.println("DelayBackend on port " + port + ", delay " + delayMs.get() + " ms");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void reply(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        try {
            ex.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException e) {
            System.out.println("client already gone (timeout on the caller side)");   // MI gave up before the delay ended
        } finally {
            ex.close();
        }
    }
}
