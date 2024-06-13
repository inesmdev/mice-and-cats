package foop;

import javax.swing.*;
import java.awt.*;
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

    public static void main(String[] args) throws Exception {
        Assets.getInstance().load();
        SwingUtilities.invokeAndWait(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
//            UIManager.put("Button.background", Color.GREEN);
        });

        try (var server = new Server()) {
            serverThread = new Thread(server::runAcceptor, "server-main");
            serverThread.setUncaughtExceptionHandler((t, e) -> stop());
            serverThread.start();

            try (
                var client1 = new Client(server.port());
                var client3 = new Client(server.port());
                var client2 = new Client(server.port()))
            {

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
