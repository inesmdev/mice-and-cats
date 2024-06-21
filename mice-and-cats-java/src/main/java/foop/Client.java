package foop;

import foop.message.*;
import foop.views.GameFrame;
import foop.world.World;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import static foop.message.Message.serialize;

@Slf4j
public class Client implements AutoCloseable, Runnable {

    private final Socket socket;
    private final String clientName = "Client_" + new Random().nextInt(100);
    private volatile boolean running = true;
    private GameFrame jFrame;

    @Getter
    private World world;

    @Getter
    private String playerName;

    @Getter
    private String gameName;

    public Client(int port) throws IOException {
        socket = new Socket("localhost", port);
    }

    private void processMessage(Message message) {
        if (message instanceof AvailableGamesMessage m) {
            jFrame.updateLobby(m);
        } else if (message instanceof GameWorldMessage m) {
            world = new World(m);
        } else if (message instanceof EntityUpdateMessage m) {
            if (world != null) {
                world.entityUpdate(m);
            }
        } else if (message instanceof GameOverMessage m) {
            gameName = null;
            world = null;
            switch (m.result()) {
                case YOU_DIED -> jFrame.showGameOverDeathView(false);
                case ALL_BUT_YOU_DIED -> jFrame.showGameOverDeathView(true);
                case VICTORY -> jFrame.showGameOverVictoryView();
            }
        } else if (message instanceof JoinedGameMessage m) {
            gameName = m.name();
            world = null;
            jFrame.showBoardView();
        } else if (message instanceof GenericErrorMessage m) {
            JOptionPane.showMessageDialog(jFrame, m.message(), "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            throw new RuntimeException("Unexpected message: " + message);
        }
    }

    @Override
    public void run() {
        try (socket) {
            SwingUtilities.invokeAndWait(() -> jFrame = new GameFrame(this));

            var in = socket.getInputStream();
            while (Main.running && this.running) {
                var message = Message.parse(in);
                log.info("{}: {}", clientName, message);
                SwingUtilities.invokeAndWait(() -> processMessage(message));
            }
        } catch (SocketException e) {
            log.error("Socket exception: {}", e.getMessage());
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

    @Override
    public void close() {
        running = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @SneakyThrows
    public synchronized void send(Message message) {
        serialize(message, this.socket.getOutputStream());
    }

    public void joinGame(String name) {
        gameName = name;
        send(new JoinGameMessage(name));
    }

    public void createGame(String name, int returnSize) {
        gameName = name;
        send(new CreateGameMessage(name, returnSize));
    }

    public void exitGame() {
        if (gameName != null) {
            send(new ExitGameMessage(gameName));
            gameName = null;
            world = null;
        }
    }

    public void setPlayerName(String name) {
        playerName = name;
        send(new InitialMessage(name));
    }
}
