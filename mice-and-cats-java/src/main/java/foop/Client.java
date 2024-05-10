package foop;

import foop.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements AutoCloseable, Runnable {

    private final Socket socket;
    private volatile boolean running = true;

    public Client(int port) throws IOException {
        socket = new Socket("localhost", port);
    }

    @Override
    public void run() {
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            SwingUtilities.invokeLater(this::createAndShowGUI);
            System.out.println(this + " sending HELLO");
            out.println("HELLO");
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

    private void createAndShowGUI() {
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Client.this.running = false;
                try {
                    Client.this.socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        f.setSize(500, 500);
        f.add(new JComponent() {
            @Override
            public void paint(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                World world = new World();
                world.render(g, getWidth(), getHeight());
            }
        });
        f.setVisible(true);
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
