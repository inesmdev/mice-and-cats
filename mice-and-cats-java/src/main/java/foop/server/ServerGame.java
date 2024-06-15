package foop.server;

import foop.Main;
import foop.message.AvailableGamesMessage;
import foop.world.World;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

@Slf4j
public class ServerGame {
    @Getter
    private final String name;
    private final int minPlayers;
    private final HashSet<Player> players = new HashSet<>();
    private World world;
    private Duration duration;
    private boolean started;
    private Thread gameThread;

    public ServerGame(String name, int minPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        duration = Duration.ofSeconds(42);
    }

    public synchronized AvailableGamesMessage.Game getLobbyInfo() {
        var ps = players.stream().map(p -> new AvailableGamesMessage.PlayerInfo(p.getName(), p.isReady())).toList();
        return new AvailableGamesMessage.Game(name, duration, ps, started);
    }

    public synchronized void removePlayer(Player player) {
        players.remove(player);
    }

    public synchronized void addPlayer(Player player) {
        players.add(player);
    }

    public synchronized boolean startIfAllReady() {
        if (players.size() >= minPlayers && players.stream().allMatch(Player::isReady)) {
            started = true;
            world = new World(new Random(), 4, 20, 5, players);
            world.sendTo(players);
            gameThread = new Thread(this::run);
            gameThread.start();
        } else {
            started = false;
        }
        return started;
    }

    public void run() {
        var r = new Random();

        while (Main.running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }

            synchronized (this) {
                if (players.isEmpty()) {
                    log.info("no players left. Stopping game.");
                    return;
                }

                log.info("server update{}", players);
                world.serverUpdate(players, duration);
            }
        }
    }


    public void movePlayer(Player player, int direction) {
        world.movePlayer(players, player, direction);
    }

    public synchronized void killDisconnectedPlayer(Player player, HashMap<String, ServerGame> games) {
        removePlayer(player);
        player.setGame(null);
        if (world != null) {
            world.killDisconnectedPlayer(player.getName(), players);
        }
        if (players.isEmpty() && !started) {
            games.remove(name);
        }
    }
}
