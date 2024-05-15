package foop;

import foop.message.Message;

import java.net.Socket;
import java.util.concurrent.LinkedTransferQueue;

public class Player {
    private String name;
    private final Socket socket;
    private boolean ready;
    private final LinkedTransferQueue<Message> toClient = new LinkedTransferQueue<>();

    public Player(Socket socket) {
        this.socket = socket;
    }

    public void send(Message message) {
        System.out.println("server->" + name + ": " + message);
        toClient.put(message);
    }

    public Message takeMessageToSend() throws InterruptedException {
        return toClient.take();
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

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", socket=" + socket +
                '}';
    }
}
