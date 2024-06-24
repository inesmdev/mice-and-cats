package foop;

import foop.message.*;
import foop.server.Player;
import foop.server.ServerGame;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
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

    /**
     * Thread methode to write messages to the player
     *
     * @param player the play which this thread belongs to
     */
    void runClientWriter(Player player) {
        try (var s = player.getSocket();
             var out = s.getOutputStream()
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
            log.info("player disconnected: {}", player);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            removePlayer(player);
        }
    }

    /**
     * Thread method to react to incoming messages
     *
     * @param player the play which this thread belongs to
     */
    void runClientReader(Player player) {
        try (var s = player.getSocket();
             var in = s.getInputStream()
        ) {
            Message message1 = Message.parse(in);
            if (message1 instanceof InitialMessage initialMessage) {
                player.setName(initialMessage.playerName());
                log.info("Server: new player {}", initialMessage.playerName());
            } else {
                throw new IOException("Expected " + InitialMessage.class.getName() + " but got " + message1);
            }

            synchronized (games) {
                player.send(generateAvailableGamesMessage());
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
                        // can't create a game with the same name:
                        if (games.containsKey(m.name())) {
                            player.send(new GenericErrorMessage("Name already exists"));
                        } else {
                            var game = new ServerGame(m.name(), m.minPlayer());
                            game.addPlayer(player);
                            player.setGame(game);
                            games.put(m.name(), game);
                            player.send(new JoinedGameMessage(game.getName()));
                        }
                        broadcastMsg(generateAvailableGamesMessage());
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
                            player.send(new GenericErrorMessage("Game doesn't exist"));
                        } else {
                            game.addPlayer(player);
                            player.setGame(game);
                            player.send(new JoinedGameMessage(game.getName()));
                        }
                        broadcastMsg(generateAvailableGamesMessage());
                    }
                } else if (message instanceof SetReadyForGameMessage m) {
                    synchronized (games) {
                        player.setReady(m.ready());
                        if (m.ready() && player.getGame() != null) {
                            if (player.getGame().startIfAllReady()) {
                                games.remove(player.getGame().getName());
                            }
                        }
                        broadcastMsg(generateAvailableGamesMessage());
                    }
                } else if (message instanceof ExitGameMessage m) {
                    synchronized (games) {
                        player.setReady(false);
                        var game = player.getGame();
                        if (game != null) {
                            game.killDisconnectedPlayer(player, games);
                        }
                        broadcastMsg(generateAvailableGamesMessage());
                    }
                } else if (message instanceof PlayerCommandMessage m) {
                    synchronized (games) {
                        var game = Objects.requireNonNull(player.getGame());
                        game.movePlayer(player, m.direction());
                    }
                } else if (message instanceof VoteMessage m) {
                    log.info("Votemessage: " + m.vote());
                    synchronized (games) {
                        var game = Objects.requireNonNull(player.getGame());
                        game.updateVote(player, m.vote());
                    }
                } else if (message instanceof InitialMessage m) {
                    // allow name changes
                    player.setName(m.playerName());
                } else {
                    throw new IOException("Unexpected message: " + message);
                }
            }
        } catch (EOFException e) {
            log.warn("player disconnected: {}", player);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            removePlayer(player);
        }
    }


    private Message generateAvailableGamesMessage() {
        return new AvailableGamesMessage(games.values().stream()
                .map(ServerGame::getLobbyInfo)
                .toList());
    }

    /**
     * methode to send a message to all players
     *
     * @param message msg to be sent
     */
    private void broadcastMsg(Message message) {
        players.keySet().forEach(p -> p.send(message));
    }


    private void removePlayer(Player player) {
        players.remove(player);
        ServerGame game = player.getGame();
        if (game != null) {
            game.removePlayer(player);
            broadcastMsg(generateAvailableGamesMessage());
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
