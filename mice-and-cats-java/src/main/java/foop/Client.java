package foop;

import foop.world.ConnectedSubwaysPlayingField;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

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
        int numRows = 10;
        int numCols = 10;
        int numSubways = 3; // Configure the maximum number of subways
        Random seed1 = new Random();

        JFrame f = new JFrame("Cats and Mice in the Subway: Subway View");
        int type0 = 0;
        ConnectedSubwaysPlayingField playingField = new ConnectedSubwaysPlayingField(seed1, type0, numRows, numCols, numSubways);


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
        f.add(playingField);
        f.setVisible(true);
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
