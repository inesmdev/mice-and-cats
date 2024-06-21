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
    @Setter
    private boolean isDead;
    @Setter
    private Integer vote;

    public Entity(int id, String name, Position position, boolean isUnderground, boolean isDead, int vote) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.isUnderground = isUnderground;
        this.isDead = isDead;
        this.vote = vote;
    }

}
