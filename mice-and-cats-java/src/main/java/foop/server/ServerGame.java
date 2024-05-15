package foop.server;

import foop.message.AvailableGamesMessage;
import foop.world.World;

import java.time.Duration;
import java.util.HashSet;

public class ServerGame {
    private final String name;
    private final HashSet<Player> players = new HashSet<>();
    private Duration duration;
    private boolean started;
    private World world;

    public ServerGame(String name) {
        this.name = name;
        duration = Duration.ofSeconds(42);
        world = new World();
    }

    public AvailableGamesMessage.Game getLobbyInfo() {
        var ps = players.stream().map(Player::getName).toList();
        return new AvailableGamesMessage.Game(name, duration, ps, started);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void startIfAllReady() {
        if (players.size() > 1 && players.stream().allMatch(Player::isReady)) {
            started = true;
            var message = world.makeGameWorldMessage();
            players.forEach(p -> p.send(message));
        }
    }
}
