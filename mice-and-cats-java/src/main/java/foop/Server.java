package foop;

import foop.message.*;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;

public class Server implements AutoCloseable {

    private final ServerSocket socket;
    private final WeakHashMap<Player, Void> players = new WeakHashMap<>();

    private final HashMap<String, LobbyEntry> lobby = new HashMap<>();

    record LobbyEntry(Duration duration, HashSet<Player> players) {
    }

    public Server() throws IOException {
        socket = new ServerSocket();
        socket.bind(null);
    }

    public int port() {
        return socket.getLocalPort();
    }

    public void runAcceptor() {
        try (socket) {
            while (Main.running) {
                var client = socket.accept();
                var player = new Player(client);
                players.put(player, null);
                new Thread(() -> runClientReader(player)).start();
                new Thread(() -> runClientWriter(player)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendAvailableGames(Player player) {
        var message = new AvailableGamesMessage(lobby.entrySet().stream()
                .map(e -> new AvailableGamesMessage.Game(e.getKey(), e.getValue().duration(), e.getValue().players().stream().map(Player::getName).toList()))
                .toList());

        if (player == null) {
            players.keySet().forEach(p -> p.send(message));
        } else {
            player.send(message);
        }
    }

    void runClientWriter(Player player) {
        try (var s = player.getSocket();
             var out = s.getOutputStream();
        ) {
            while (true) {
                var message = player.takeMessageToSend();
                Message.serialize(message, out);
            }
        } catch (EOFException e) {
            System.out.println("player disconnected: " + player);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            players.remove(player);
            for (var entry : lobby.values()) {
                entry.players.remove(player);
            }
            sendAvailableGames(null);
        }
    }

    void runClientReader(Player player) {
        try (var s = player.getSocket();
             var in = s.getInputStream();
        ) {
            var initialMessage = Message.parse(in).into(InitialMessage.class);
            player.setName(initialMessage.playerName());
            System.out.println("Server: new player " + initialMessage.playerName());

            synchronized (lobby) {
                sendAvailableGames(player);
            }

            while (true) {
                var message = Message.parse(in);

                if (message instanceof CreateGameMessage m) {
                    synchronized (lobby) {
                        player.setReady(false);
                        for (var entry : lobby.values()) {
                            entry.players.remove(player);
                        }
                        if (lobby.containsKey(m.name())) {
                            player.send(new GenericResponseMessage("Name already exists", true));
                        } else {
                            var entry = new LobbyEntry(m.duration(), new HashSet<>());
                            entry.players.add(player);
                            lobby.put(m.name(), entry);
                            player.send(new GenericResponseMessage("", false));
                        }
                        sendAvailableGames(null);
                    }
                } else if (message instanceof JoinGameMessage m) {
                    synchronized (lobby) {
                        player.setReady(false);
                        for (var entry : lobby.values()) {
                            entry.players.remove(player);
                        }
                        var entry = lobby.get(m.name());
                        if (entry == null) {
                            player.send(new GenericResponseMessage("Game doesn't exist", true));
                        } else {
                            entry.players.add(player);
                            player.send(new GenericResponseMessage("", false));
                        }
                        sendAvailableGames(null);
                    }
                } else if (message instanceof SetReadyForGameMessage m) {
                    synchronized (lobby) {
                        player.setReady(m.ready());
                        sendAvailableGames(null);
                    }
                } else {
                    throw new IOException("Unexpected message: " + message);
                }
            }
        } catch (EOFException e) {
            System.out.println("player disconnected: " + player);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            players.remove(player);
            for (var entry : lobby.values()) {
                entry.players.remove(player);
            }
            sendAvailableGames(null);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
        synchronized (players) {
            for (var player : players.keySet()) {
                player.getSocket().close();
            }
        }
    }
}
