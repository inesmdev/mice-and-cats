package foop.world;

public record Position(int x, int y) {

    public double distanceTo(Position other){
        int dx = x - other.x;
        int dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
