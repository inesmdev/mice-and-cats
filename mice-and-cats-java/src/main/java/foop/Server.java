package foop;

import foop.message.*;
import foop.server.Player;
import foop.server.ServerGame;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Server implements AutoCloseable {

    private final ServerSocket socket;
    private final HashMap<Player, Void> players = new HashMap<>();
    private final HashMap<String, ServerGame> games = new HashMap<>();

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

    void runClientWriter(Player player) {
        try (var s = player.getSocket();
             var out = s.getOutputStream();
        ) {
            while (true) {
                var message = player.pollMessageToSend(1, TimeUnit.SECONDS);
                if (message != null) {
                    Message.serialize(message, out);
                } else if (socket.isClosed()) {
                    break;
                }
            }
        } catch (EOFException e) {
            System.out.println("player disconnected: " + player);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            removePlayer(player);
        }
    }

    void runClientReader(Player player) {
        try (var s = player.getSocket();
             var in = s.getInputStream();
        ) {
            var initialMessage = Message.parse(in).into(InitialMessage.class);
            player.setName(initialMessage.playerName());
            System.out.println("Server: new player " + initialMessage.playerName());

            synchronized (games) {
                sendAvailableGames(player);
            }

            while (true) {
                var message = Message.parse(in);

                if (message instanceof CreateGameMessage m) {
                    synchronized (games) {
                        player.setReady(false);
                        if (player.getGame() != null) {
                            player.getGame().removePlayer(player);
                            player.setGame(null);
                        }
                        if (games.containsKey(m.name())) {
                            player.send(new GenericResponseMessage("Name already exists", true));
                        } else {
                            var game = new ServerGame(m.name());
                            game.addPlayer(player);
                            player.setGame(game);
                            games.put(m.name(), game);
                            player.send(new GenericResponseMessage("", false));
                        }
                        sendAvailableGames(null);
                    }
                } else if (message instanceof JoinGameMessage m) {
                    synchronized (games) {
                        player.setReady(false);
                        if (player.getGame() != null) {
                            player.getGame().removePlayer(player);
                            player.setGame(null);
                        }
                        var game = games.get(m.name());
                        if (game == null) {
                            player.send(new GenericResponseMessage("Game doesn't exist", true));
                        } else {
                            game.addPlayer(player);
                            player.setGame(game);
                            player.send(new GenericResponseMessage("", false));
                        }
                        sendAvailableGames(null);
                    }
                } else if (message instanceof SetReadyForGameMessage m) {
                    synchronized (games) {
                        player.setReady(m.ready());
                        if (m.ready() && player.getGame() != null) {
                            player.getGame().startIfAllReady();
                        }
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
            removePlayer(player);
        }
    }

    private void sendAvailableGames(Player player) {
        var message = new AvailableGamesMessage(games.values().stream()
                .map(ServerGame::getLobbyInfo)
                .toList());

        if (player == null) {
            players.keySet().forEach(p -> p.send(message));
        } else {
            player.send(message);
        }
    }

    private void removePlayer(Player player) {
        players.remove(player);
        ServerGame game = player.getGame();
        if (game != null) {
            game.removePlayer(player);
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
