package foop.server;

import foop.message.AvailableGamesMessage;

import java.time.Duration;
import java.util.HashSet;

public class ServerGame {
    private final String name;
    private Duration duration;
    private final HashSet<Player> players = new HashSet<>();
    private boolean started;

    public ServerGame(String name) {
        this.name = name;
        duration = Duration.ofSeconds(42);
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
        if (players.stream().allMatch(p -> p.isReady())) {
            started = true;
        }
    }
}
