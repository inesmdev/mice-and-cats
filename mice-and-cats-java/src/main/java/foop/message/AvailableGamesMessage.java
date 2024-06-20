package foop.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public record AvailableGamesMessage(List<Game> games) implements Message {

    public record Game(String name, Duration duration, List<PlayerInfo> players, boolean started) {
    }

    public record PlayerInfo(String name, boolean ready) {
    }

    public static final int TAG = 3;

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(games.size());
        for (Game game : games) {
            out.writeUTF(game.name());
            out.writeLong(game.duration().getSeconds());
            out.writeInt(game.duration().getNano());
            out.writeInt(game.players().size());
            for (var player : game.players()) {
                out.writeUTF(player.name());
                out.writeBoolean(player.ready());
            }
            out.writeBoolean(game.started());
        }
    }

    public static AvailableGamesMessage parse(DataInputStream in) throws IOException {
        var count = in.readInt();
        var games = new Game[count];
        for (int i = 0; i < games.length; ++i) {
            var name = in.readUTF();
            var seconds = in.readLong();
            var nano = in.readInt();
            var duration = Duration.ofSeconds(seconds, nano);
            var playerCount = in.readInt();
            var players = new PlayerInfo[playerCount];
            for (int j = 0; j < players.length; ++j) {
                var playerName = in.readUTF();
                var ready = in.readBoolean();
                players[j] = new PlayerInfo(playerName, ready);
            }
            boolean started = in.readBoolean();
            games[i] = new Game(name, duration, List.of(players), started);
        }
        return new AvailableGamesMessage(List.of(games));
    }
}

