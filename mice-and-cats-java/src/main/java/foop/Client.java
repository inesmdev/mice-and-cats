package foop;

import foop.message.*;
import foop.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.time.Duration;
import java.util.Random;

import static foop.message.Message.serialize;

public class Client implements AutoCloseable, Runnable {

    private final Socket socket;
    private volatile boolean running = true;
    private JFrame jFrame;
    private final String playerName = "Player" + new Random().nextInt(100);
    private AvailableGamesMessage lobby;

    public Client(int port) throws IOException {
        socket = new Socket("localhost", port);
    }


    Message receiveNext(InputStream in) throws IOException {
        while (true) {
            var message = Message.parse(in);
            if (message instanceof AvailableGamesMessage m) {
                lobby = m;
            } else {
                return message;
            }
        }
    }

    @Override
    public void run() {
        try (socket) {
            SwingUtilities.invokeAndWait(this::createAndShowGUI);

            var in = socket.getInputStream();
            var out = socket.getOutputStream();

            serialize(new InitialMessage(playerName), out);

            serialize(new CreateGameMessage("game1"), out);
            if (receiveNext(in).into(GenericResponseMessage.class).error()) {
                serialize(new JoinGameMessage("game1"), out);
                if (receiveNext(in).into(GenericResponseMessage.class).error()) {
                    throw new IOException("Could not join game");
                }
            }
            serialize(new SetReadyForGameMessage(true), out);

            while (Main.running && this.running) {
                var message = receiveNext(in);
                System.out.println(playerName + ": " + message);
            }
        } catch (IOException | InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            SwingUtilities.invokeLater(() -> {
                if (jFrame != null) {
                    jFrame.dispose();
                }
            });
            System.out.println(playerName + " exited");
        }
    }

    private void createAndShowGUI() {
        JFrame f = new JFrame(playerName);
        jFrame = f;
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
