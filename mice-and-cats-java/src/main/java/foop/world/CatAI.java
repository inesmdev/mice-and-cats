package foop.world;

import java.util.function.Consumer;

import static foop.world.Type.MOUSE;

/**
 * This AI controls a single cat.
 */
public class CatAI implements EntityAI {

    private final Entity controlled;

    private Position nextGoal;
    private Position lastKnownMousePosition;

    /**
     * @param entity The cat to control.
     */
    public CatAI(Entity entity) {
        if (entity.getType() != Type.CAT)
            throw new IllegalArgumentException("Inferior entity is not a cat: " + entity);
        this.controlled = entity;
    }

    @Override
    public boolean update(World world, Consumer<Entity> onUpdate) {
        if (controlled.isDead()) {
            return false;
        }

        // if we see a mouse we target override the next goal:
        int controlledSubway = world.getSubway(controlled);
        var nearestMouse = world.findNearestEntity(controlled.getPosition(), e -> e.getType() == MOUSE && world.getSubway(e) == controlledSubway);
        if (nearestMouse != null) {
            nextGoal = nearestMouse.getPosition();
            lastKnownMousePosition = nearestMouse.getPosition();
        }

        // if we don't have a goal we patrol between the last known mouse position and a random position
        if (nextGoal == null || controlled.getPosition().equals(nextGoal)) {
            if (lastKnownMousePosition != null && !controlled.getPosition().equals(lastKnownMousePosition)) {
                nextGoal = lastKnownMousePosition;
            } else {
                nextGoal = world.getRandomPosition();
            }
        }

        // move entity
        if (controlledSubway != 0)
            throw new RuntimeException("Cat doesn't know legal moves inside subways");
        var nextPosition = controlled.getPosition().stepTowards(nextGoal);
        controlled.setPosition(nextPosition);
        onUpdate.accept(controlled);

        return true; // keep updating
    }
}
