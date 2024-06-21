package foop;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static volatile boolean running = true;
    public static Thread serverThread;
    public static Set<Thread> clientThreads = new HashSet<>();

    public static void stop() {
        running = false;
        if (serverThread != null) {
            serverThread.interrupt();
        }
        for (Thread client : clientThreads) {
            client.interrupt();
        }
    }

    public static void usage(String error) {
        System.out.println("Error: " + error);
        System.out.println("Usage: java -jar mice-and-cats.jar Main <command> [args]");
        System.out.println("COMMANDS:");
        System.out.println("    server <host> <port>");
        System.out.println("    client <host> <port>");
        System.out.println("    dev");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            usage("Missing command line arguments");
            //runClient(DEFAULT_HOST, DEFAULT_PORT);
            return;
        }
        switch (args[0]) {
            case "client", "server" -> {
                if (args.length != 3) {
                    usage("Expected <host> and <port>");
                }
                String host = args[1];
                int port;
                try {
                    port = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    usage("Invalid port number");
                    return;
                }
                if (args[0].equals("server")) {
                    runServer(new InetSocketAddress(host, port));
                } else {
                    runClient(host, port);
                }
            }
            case "dev" -> {
                if (args.length != 1) {
                    usage("Unexpected arguments");
                }
                runDev();
            }
            default -> usage("Unknown subcommand: " + args[0]);
        }
    }

    private static void runServer(InetSocketAddress endpoint) throws Exception {
        try (var server = new Server(endpoint)) {
            server.runAcceptor();
        } finally {
            stop();
        }
    }

    private static void runClient(String host, int port) throws Exception {
        initGui();

        try (var client = new Client(host, port)) {
            client.run();
        } finally {
            stop();
        }
    }

    private static void initGui() throws Exception {
        Assets.getInstance().load();
        SwingUtilities.invokeAndWait(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
//            UIManager.put("Button.background", Color.GREEN);
        });
    }

    private static void runDev() throws Exception {
        initGui();

        var address = new InetSocketAddress("localhost", 0);
        try (var server = new Server(address)) {
            serverThread = new Thread(server::runAcceptor, "server-main");
            serverThread.setUncaughtExceptionHandler((t, e) -> stop());
            serverThread.start();

            try (
                    var client1 = new Client("localhost", server.port());
                    var client3 = new Client("localhost", server.port());
                    var client2 = new Client("localhost", server.port())) {

                clientThreads.add(new Thread(client1, "client1-main"));
//                client1Thread.setUncaughtExceptionHandler((t, e) -> stop());

                clientThreads.add(new Thread(client2, "client2-main"));
                clientThreads.add(new Thread(client3, "client3-main"));
//                client2Thread.setUncaughtExceptionHandler((t, e) -> stop());

                // start all the clients
                for (var client : clientThreads) {
                    client.start();
                }

                // just wait for clients to exit or crash
                for (var client : clientThreads) {
                    client.join();
                }
            }
        } finally {
            stop();
        }
    }
}
