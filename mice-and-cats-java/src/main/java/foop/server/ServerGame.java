package foop.server;

import foop.Main;
import foop.message.AvailableGamesMessage;
import foop.world.World;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashSet;
import java.util.Random;

@Slf4j
public class ServerGame {
    private final String name;
    private final HashSet<Player> players = new HashSet<>();
    private final World world;
    private Duration duration;
    private boolean started;

    public ServerGame(String name) {
        this.name = name;
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
        if (players.size() > 1 && players.stream().allMatch(Player::isReady)) {
            started = true;
            world.sendTo(players);
            new Thread(this::run).start();
        }
    }

    public void run() {
        var r = new Random();

        while (Main.running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            synchronized (this) {
                log.info("server update{}", players);
                world.serverUpdate(players, duration);
            }
        }
    }
}
