package foop;

import foop.message.AvailableGamesMessage;
import foop.message.EntityUpdateMessage;
import foop.message.GameWorldMessage;
import foop.message.Message;
import foop.views.GameFrame;
import foop.world.World;
import lombok.Getter;
import lombok.Setter;
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

@Setter
@Getter
@Slf4j
public class Client implements AutoCloseable, Runnable {

    private final Socket socket;
    private String playerName = "Player" + new Random().nextInt(100);
    private volatile boolean running = true;
    private JFrame jFrame;
    private Consumer<AvailableGamesMessage> updateLobby;
    private World world;
    private DefaultListModel<AvailableGamesMessage.Game> lobbyListModel = new DefaultListModel<>();

    public Client(int port) throws IOException {
        socket = new Socket("localhost", port);
    }


    Message receiveNext(InputStream in) throws IOException {
        while (true) {
            var message = Message.parse(in);
            if (message instanceof AvailableGamesMessage m) {
                SwingUtilities.invokeLater(() -> updateLobby.accept(m));
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

//            if (receiveNext(in).into(GenericResponseMessage.class).error()) {
//                serialize(new JoinGameMessage("game1"), out);
//                if (receiveNext(in).into(GenericResponseMessage.class).error()) {
//                    throw new IOException("Could not join game");
//                }
//            }

            while (Main.running && this.running) {
                var message = receiveNext(in);
                log.info("{}: {}", playerName, message);
            }
        } catch (IOException | InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            SwingUtilities.invokeLater(() -> {
                if (jFrame != null) {
                    jFrame.dispose();
                }
            });
            log.info("{} exited", playerName);
        }
    }

    private void createAndShowGUI() {

        this.jFrame = new GameFrame(this);
        this.updateLobby = updateLobby();
    }

    @Override
    public void close() throws Exception {
        this.socket.close();
    }


    @SneakyThrows
    public synchronized void send(Message message) {
        serialize(message, this.socket.getOutputStream());
    }

    public synchronized Consumer<AvailableGamesMessage> updateLobby() {
        return availableGamesMessage -> {
            JList<AvailableGamesMessage> lobbyList = new JList<>();
            int index = lobbyList.getSelectedIndex();
            var selected = index != -1 ? lobbyListModel.get(index).name() : null;

            lobbyListModel.removeAllElements();
            lobbyListModel.addAll(availableGamesMessage.games());

            if (selected != null) {
                for (int i = 0; i < lobbyListModel.size(); ++i) {
                    if (selected.equals(lobbyListModel.get(i).name())) {
                        lobbyList.setSelectedIndex(i);
                        break;
                    }
                }
            }
        };
    }
}
