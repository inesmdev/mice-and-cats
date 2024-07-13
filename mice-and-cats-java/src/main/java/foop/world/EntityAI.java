package foop.world;

import java.util.function.Consumer;

/**
 * Represents artificial intelligence that controls one or more entities.
 */
public interface EntityAI {

    /**
     * Invoked once per tick until it returns false.
     *
     * @param world The world.
     * @param onUpdate Called whenever an entity is updated. This is deliberately abstract
     *                 in case we want to run AIs on the client later.
     * @return whether to keep updating this AI
     */
    boolean update(World world, Consumer<Entity> onUpdate);
}
