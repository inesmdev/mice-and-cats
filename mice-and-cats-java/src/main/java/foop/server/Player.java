package foop.server;

import foop.message.GameOverMessage;
import foop.message.Message;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Player {
    private String name;
    private final Socket socket;
    private boolean ready;
    private final LinkedTransferQueue<Message> toClient = new LinkedTransferQueue<>();
    private ServerGame game;

    public Player(Socket socket) {
        this.socket = socket;
    }

    public void send(Message message) {
        log.info("server->{}: {}", name, message);
        toClient.put(message);
    }

    public Message pollMessageToSend(long timeout, TimeUnit unit) throws InterruptedException {
        return toClient.poll(timeout, unit);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public ServerGame getGame() {
        return game;
    }

    public void setGame(ServerGame game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", socket=" + socket +
                '}';
    }

    public void gameOver(GameOverMessage gameOverMessage) {
        send(gameOverMessage);
        getGame().removePlayer(this);
        game = null;
    }
}
