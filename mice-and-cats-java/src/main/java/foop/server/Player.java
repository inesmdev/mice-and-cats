package foop.server;

import foop.message.GameOverMessage;
import foop.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Player {
    @Setter
    @Getter
    private String name;
    @Getter
    private final Socket socket;
    @Setter
    @Getter
    private boolean ready;
    private final LinkedTransferQueue<Message> toClient = new LinkedTransferQueue<>();
    @Setter
    @Getter
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
