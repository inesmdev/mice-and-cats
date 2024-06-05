package foop.server;

import foop.Main;
import foop.message.AvailableGamesMessage;
import foop.world.World;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashSet;
import java.util.Random;

@Slf4j
public class ServerGame {
    private final String name;
    private final int minPlayers;
    private final HashSet<Player> players = new HashSet<>();
    private final World world;
    private Duration duration;
    private boolean started;
    private Thread gameThread;

    public ServerGame(String name, int minPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        duration = Duration.ofSeconds(42);
        world = new World(new Random(), 0, 4 ,16, 16);
    }

    public synchronized AvailableGamesMessage.Game getLobbyInfo() {
        var ps = players.stream().map(Player::getName).toList();
        return new AvailableGamesMessage.Game(name, duration, ps, started);
    }

    public synchronized void removePlayer(Player player) {
        players.remove(player);
    }

    public synchronized void addPlayer(Player player) {
        players.add(player);
    }

    public synchronized void startIfAllReady() {
        if (players.size() >= minPlayers && players.stream().allMatch(Player::isReady)) {
            started = true;
            world.sendTo(players);
            gameThread = new  Thread(this::run);
            gameThread.start();
        } else {
            started = false;
        }
    }

    public synchronized void stop() {
        started = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
    }

    public void run() {
        var r = new Random();

        while (Main.running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                if (players.size() <= 1) {
                    log.info("only one player left. Stopping game.");
                    return;
                }
            }

            synchronized (this) {
                log.info("server update{}", players);
                world.serverUpdate(players, duration);
            }
        }
    }
}
