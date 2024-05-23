package foop;

import foop.views.GameFrame;
import foop.views.View;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Setter
@Getter
@Slf4j
public class Client implements AutoCloseable, Runnable {

    private final Socket socket;
    private volatile boolean running = true;
    private View view;

    public Client(int port) throws IOException {
        socket = new Socket("localhost", port);
    }

    @Override
    public void run() {
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            SwingUtilities.invokeLater(this::view);
            while (Main.running && this.running) {
                var line = in.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(this + " received " + line);
                out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println(this + " exited");
        }
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }

    private void view() {


        GameFrame frame = new GameFrame(this);
        frame.setVisible(true);
        
    }
}
