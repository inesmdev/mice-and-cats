package foop.message;

import foop.world.Position;
import foop.world.Subway;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record GameWorldMessage(int[][] subwayTiles, List<Subway> subways) implements Message {

    public static final int TAG = 7;

    public static GameWorldMessage parse(DataInputStream in) throws IOException {
        var subwayTiles = new int[in.readInt()][];
        for (int i = 0; i < subwayTiles.length; i++) {
            subwayTiles[i] = new int[in.readInt()];
            for (int j = 0; j < subwayTiles[i].length; j++) {
                subwayTiles[i][j] = in.readInt();
            }
        }
        var subways = new Subway[in.readInt()];
        for (int i = 0; i < subways.length; i++) {
            var color = new Color(in.readInt(), true);
            var exits = new Position[in.readInt()];
            for (int j = 0; j < exits.length; j++) {
                exits[j] = new Position(in.readInt(), in.readInt());
            }
            var subwayCells = new Point[in.readInt()];
            for (int j = 0; j < subwayCells.length; j++) {
                subwayCells[j] = new Point(in.readInt(), in.readInt());
            }
            subways[i] = new Subway(i + 1, color, List.of(exits), Arrays.stream(subwayCells).toList());
        }
        return new GameWorldMessage(subwayTiles, List.of(subways));
    }

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(subwayTiles.length);
        for (int[] row : subwayTiles) {
            out.writeInt(row.length);
            for (int tile : row) {
                out.writeInt(tile);
            }
        }
        out.writeInt(subways.size());
        for (Subway subway : subways) {
            out.writeInt(subway.color().getRGB());
            out.writeInt(subway.exits().size());
            for (var exit : subway.exits()) {
                out.writeInt(exit.x());
                out.writeInt(exit.y());
            }
            out.writeInt(subway.subwayCells().size());
            for (var subwayCell : subway.subwayCells()) {
                out.writeInt(subwayCell.x);
                out.writeInt(subwayCell.y);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameWorldMessage that)) return false;
        return Objects.deepEquals(subwayTiles, that.subwayTiles) && Objects.equals(subways, that.subways);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(subwayTiles), subways);
    }
}

