package it.unicam.cs.mdp.vectorrace.model.core;

import java.util.Arrays;

/**
 * Represents the possible acceleration vectors in the Vector Race game.
 * Each acceleration is represented by a vector (dx, dy) with values in {-1, 0, 1},
 * corresponding to the eight possible directions of movement plus no movement.
 *
 * <p>The acceleration types are organized in a compass-like pattern:
 * <ul>
 *   <li>NORTH_WEST, NORTH, NORTH_EAST represent upward movements (-1 in x)</li>
 *   <li>WEST, NONE, EAST represent horizontal movements (0 in x)</li>
 *   <li>SOUTH_WEST, SOUTH, SOUTH_EAST represent downward movements (+1 in x)</li>
 * </ul>
 */
public enum AccelerationType {
    NORTH_WEST(new Vector(-1, -1)),
    NORTH(new Vector(-1, 0)),
    NORTH_EAST(new Vector(-1, 1)),
    WEST(new Vector(0, -1)),
    NONE(new Vector(0, 0)),
    EAST(new Vector(0, 1)),
    SOUTH_WEST(new Vector(1, -1)),
    SOUTH(new Vector(1, 0)),
    SOUTH_EAST(new Vector(1, 1));

    private final Vector acceleration;

    AccelerationType(Vector acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Gets the acceleration vector associated with this acceleration type.
     * 
     * @return The vector representing the acceleration in 2D space.
     */
    public Vector getVector() {
        return acceleration;
    }

    /**
     * Gets all possible acceleration vectors in the game.
     * This method provides access to all available movement options,
     * including no movement (NONE).
     *
     * @return An array containing all possible acceleration vectors.
     */
    public static Vector[] getAllVectors() {
        return Arrays.stream(values())
                    .map(AccelerationType::getVector)
                    .toArray(Vector[]::new);
    }
}