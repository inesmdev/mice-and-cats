package foop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.WeakHashMap;

public class Server implements AutoCloseable {

    private final ServerSocket socket;
    private final WeakHashMap<Socket, Void> clients = new WeakHashMap<>();

    public Server() throws IOException {
        socket = new ServerSocket();
        socket.bind(null);
    }

    public int port() {
        return socket.getLocalPort();
    }

    public void runAcceptor() {
        while (Main.running) {
            try {
                var client = socket.accept();
                clients.put(client, null);
                new Thread(() -> runClient(client)).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void runLobby() {
        while (Main.running) {

        }
    }

    void runClient(Socket client) {
        try (var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             var out = new PrintWriter(client.getOutputStream(), true)
        ) {
            while (true) {
                var line = in.readLine();
                if (line == null) {
                    break;
                }
                Thread.sleep(1000);
                out.println(line + "!");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
        for (var client : clients.keySet()) {
            client.close();
        }
    }
}
