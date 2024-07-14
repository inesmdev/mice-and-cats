package foop.world;

public record Position(int x, int y) {

    /**
     * @param other Another point.
     * @return The Euclidean distance to the other point.
     */
    public double distanceTo(Position other) {
        int dx = x - other.x;
        int dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @param other The target.
     * @return A point that is one step closer to the target.
     */
    public Position stepTowards(Position other) {
        var dx = other.x - x;
        var dy = other.y - y;
        dx = Math.min(1, Math.max(-1, dx));
        dy = Math.min(1, Math.max(-1, dy));
        return new Position(x + dx, y + dy);
    }
}
