package foop.world;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class Entity {
    private final int id;
    @Setter
    private String name;
    @Setter
    private Position position;
    @Setter
    private boolean isUnderground;

    public Entity(int id, String name, Position position, boolean isUnderground) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.isUnderground = isUnderground;
    }

}
