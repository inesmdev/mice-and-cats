package foop;

public class Main {
    public static volatile boolean running = true;
    public static Thread serverThread;
    public static Thread client1Thread;
    public static Thread client2Thread;

    public static void stop() {
        running = false;
        if (serverThread != null) {
            serverThread.interrupt();
        }
        if (client1Thread != null) {
            client1Thread.interrupt();
        }
        if (client2Thread != null) {
            client2Thread.interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        try (var server = new Server()) {
            serverThread = new Thread(server, "server-main");
            serverThread.setUncaughtExceptionHandler((t, e) -> stop());
            serverThread.start();

            try (var client1 = new Client(server.port()); var client2 = new Client(server.port())) {
                client1Thread = new Thread(client1, "client1-main");
                client1Thread.setUncaughtExceptionHandler((t, e) -> stop());
                client1Thread.start();

                client2Thread = new Thread(client2, "client2-main");
                client2Thread.setUncaughtExceptionHandler((t, e) -> stop());
                client2Thread.start();

                // just wait for clients to exit or crash
                client1Thread.join();
                client2Thread.join();
            }
        } finally {
            stop();
        }
    }
}
