package foop;

import foop.message.*;
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

    Message receiveNext(InputStream in) throws IOException {
        while (true) {
            var message = Message.parse(in);
            if (message instanceof AvailableGamesMessage m) {
                SwingUtilities.invokeLater(() -> jFrame.updateLobby(m));
            } else if (message instanceof GameWorldMessage m) {
                world = new World(m);
            } else if (message instanceof EntityUpdateMessage m) {
                world.entityUpdate(m);
            } else if (message instanceof GameOverMessage m) {
                gameName = null;
                world = null;
                switch (m.result()) {
                    case YOU_DIED -> jFrame.showGameOverDeathView(false);
                    case ALL_BUT_YOU_DIED -> jFrame.showGameOverDeathView(true);
                    case VICTORY -> jFrame.showGameOverVictoryView();
                }
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
