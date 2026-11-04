import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Troubleshooting Session 01 - load client.
 *
 * Sends the same GET request from many users (threads) at the same time and prints
 * every response, then a summary. Needs only JDK 11 or later; no build step:
 *
 *   java LoadClient.java                                  (defaults below)
 *   java LoadClient.java <url> <users> <requestsPerUser>
 *
 *   java LoadClient.java http://localhost:8290/services/AccountStatementService/statement/1001 80 1
 */
public class LoadClient {

    static final String DEFAULT_URL = "http://localhost:8290/services/AccountStatementService/statement/1001";
    static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    record Result(int user, int request, int status, long millis, String body) { }

    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : DEFAULT_URL;
        int users = args.length > 1 ? Integer.parseInt(args[1]) : 80;
        int requestsPerUser = args.length > 2 ? Integer.parseInt(args[2]) : 1;

        System.out.printf("Target : %s%nUsers  : %d (at the same time)%nEach   : %d request(s)%n%n",
                url, users, requestsPerUser);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .executor(Executors.newFixedThreadPool(users))
                .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(60))
                .GET().build();

        List<Result> results = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(users);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(users);
        long begin = System.currentTimeMillis();

        for (int u = 1; u <= users; u++) {
            final int user = u;
            pool.submit(() -> {
                try {
                    start.await();
                    for (int r = 1; r <= requestsPerUser; r++) {
                        long t0 = System.currentTimeMillis();
                        int status;
                        String body;
                        try {
                            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                            status = resp.statusCode();
                            body = resp.body();
                        } catch (Exception e) {
                            status = -1;
                            body = e.getClass().getSimpleName() + ": " + e.getMessage();
                        }
                        Result res = new Result(user, r, status, System.currentTimeMillis() - t0, body);
                        synchronized (results) {
                            results.add(res);
                        }
                        print(res);
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();          // release all users together
        done.await();
        pool.shutdown();
        summary(results, System.currentTimeMillis() - begin);
        System.exit(0);
    }

    static void print(Result r) {
        String line = String.format("%s  user-%02d #%d  HTTP %s  %5d ms", LocalTime.now().format(TIME),
                r.user(), r.request(), r.status() < 0 ? "ERR" : r.status(), r.millis());
        if (r.status() < 0) {       // client-side problem only (for example connection refused)
            line += "  " + oneLine(r.body(), 160);
        }                           // the server's error text is not printed: find the cause in the MI log
        System.out.println(line);
    }

    static void summary(List<Result> results, long elapsed) {
        Map<String, Integer> byStatus = new TreeMap<>();
        long ok = 0, min = Long.MAX_VALUE, max = 0, total = 0;
        for (Result r : results) {
            byStatus.merge(r.status() < 0 ? "ERR" : String.valueOf(r.status()), 1, Integer::sum);
            if (r.status() == 200) ok++;
            min = Math.min(min, r.millis());
            max = Math.max(max, r.millis());
            total += r.millis();
        }
        System.out.println("\n================ Summary ================");
        System.out.printf("Requests   : %d%n", results.size());
        System.out.printf("Successful : %d%n", ok);
        System.out.printf("Failed     : %d%n", results.size() - ok);
        System.out.printf("By status  : %s%n", byStatus);
        System.out.printf("Time (ms)  : min %d / avg %d / max %d%n", min, total / Math.max(1, results.size()), max);
        System.out.printf("Elapsed    : %.1f s%n", elapsed / 1000.0);
        results.stream().filter(r -> r.status() != 200).findFirst().ifPresent(r ->
                System.out.printf("First failure: HTTP %s after %d ms%n", r.status() < 0 ? "ERR" : r.status(), r.millis()));
    }

    static String oneLine(String s, int max) {
        String t = s == null ? "" : s.replaceAll("\\s+", " ").trim();
        return t.length() > max ? t.substring(0, max) + "..." : t;
    }
}
