package it.unicam.cs.mdp.vectorrace.model.core;

import java.util.Objects;

/**
 * Represents a position in 2D space on the race track using integer coordinates.
 * This immutable class provides basic position manipulation operations and 
 * calculations needed for the Vector Race game mechanics.
 * 
 * <p>The coordinate system uses:
 * <ul>
 *   <li>x: horizontal position (increases from left to right)</li>
 *   <li>y: vertical position (increases from top to bottom)</li>
 * </ul>
 */
public class Position {

    private final int x;
    private final int y;

    /**
     * Creates a new Position with the specified coordinates.
     *
     * @param x The horizontal coordinate.
     * @param y The vertical coordinate.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the horizontal (x) coordinate of this position.
     *
     * @return The x coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the vertical (y) coordinate of this position.
     *
     * @return The y coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Calculates the Euclidean distance to another position.
     * This is useful for determining proximity between positions
     * on the race track.
     *
     * @param other The other position to calculate distance to.
     * @return The Euclidean distance between this position and the other position.
     */
    public double distanceTo(Position other) {
        int dx = other.getX() - this.x;
        int dy = other.getY() - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Creates a new position by adding a vector to this position.
     * This operation represents movement in the game, where the vector
     * can be either velocity or acceleration.
     *
     * @param vector The vector to add to this position.
     * @return A new Position that results from adding the vector to this position.
     */
    public Position add(Vector vector) {
        return new Position(x + vector.getDx(), y + vector.getDy());
    }

    /**
     * Moves this position by the specified vector.
     * This is an alias for {@link #add(Vector)} to provide a more intuitive API
     * when dealing with movement operations.
     *
     * @param vector The vector representing the movement.
     * @return A new Position after applying the movement.
     */
    public Position move(Vector vector) {
        return this.add(vector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position pos = (Position) o;
        return this.x == pos.getX() && this.y == pos.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
