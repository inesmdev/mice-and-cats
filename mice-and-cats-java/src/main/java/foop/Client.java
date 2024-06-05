package foop;

import foop.message.AvailableGamesMessage;
import foop.message.EntityUpdateMessage;
import foop.message.GameWorldMessage;
import foop.message.Message;
import foop.views.GameFrame;
import foop.world.World;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Random;
import java.util.function.Consumer;

import static foop.message.Message.serialize;

@Getter
@Slf4j
public class Client implements AutoCloseable, Runnable {

    private final Socket socket;
    private final String clientName = "Client_" + new Random().nextInt(100);
    private volatile boolean running = true;
    private JFrame jFrame;
    private World world;
    private final DefaultListModel<AvailableGamesMessage.Game> lobbyListModel = new DefaultListModel<>();

    public Client(int port) throws IOException {
        socket = new Socket("localhost", port);
    }


    Message receiveNext(InputStream in) throws IOException {
        while (true) {
            var message = Message.parse(in);
            if (message instanceof AvailableGamesMessage m) {
                updateLobby(m);
                //SwingUtilities.invokeLater(() -> updateLobby.accept(m));
            } else if (message instanceof GameWorldMessage m) {
                world = new World(m);
            } else if (message instanceof EntityUpdateMessage m) {
                world.entityUpdate(m);
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

            while (Main.running && this.running) {
                var message = receiveNext(in);
                log.info("{}: {}", clientName, message);
            }
        } catch (IOException | InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            SwingUtilities.invokeLater(() -> {
                if (jFrame != null) {
                    jFrame.dispose();
                }
            });
            log.info("{} exited", clientName);
        }
    }

    private void createAndShowGUI() {
        this.jFrame = new GameFrame(this);
    }

    @Override
    public void close() {
        running = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @SneakyThrows
    public synchronized void send(Message message) {
        serialize(message, this.socket.getOutputStream());
    }

    private synchronized void updateLobby(AvailableGamesMessage m) {
        lobbyListModel.removeAllElements();
        lobbyListModel.addAll(m.games());
    }
}
