package foop.world;

public class Entity {
    private final int id;
    private String name;
    private Position position;

    public Entity(int id, String name, Position position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}
