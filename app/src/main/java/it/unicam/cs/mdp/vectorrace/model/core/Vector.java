package it.unicam.cs.mdp.vectorrace.model.core;

/**
 * Represents a two-dimensional vector used for velocity and acceleration in the Vector Race game.
 * This immutable class provides basic vector operations and maintains the game's physics rules.
 * 
 * <p>The vector components are:
 * <ul>
 *   <li>dx: horizontal component (positive values move right, negative move left)</li>
 *   <li>dy: vertical component (positive values move down, negative move up)</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Immutable implementation ensures thread safety</li>
 *   <li>Provides vector arithmetic operations (add, subtract)</li>
 *   <li>Includes utility methods for game mechanics</li>
 * </ul>
 */
public class Vector {

    private final int dx;
    private final int dy;
    
    /**
     * Represents a zero vector (0,0). Useful as a neutral element for vector operations
     * and for representing no movement.
     */
    public static final Vector ZERO = new Vector(0, 0);

    /**
     * Creates a new Vector with the specified components.
     *
     * @param dx The horizontal component of the vector.
     * @param dy The vertical component of the vector.
     */
    public Vector(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Gets the horizontal component of this vector.
     *
     * @return The horizontal (dx) component.
     */
    public int getDx() {
        return this.dx;
    }

    /**
     * Gets the vertical component of this vector.
     *
     * @return The vertical (dy) component.
     */
    public int getDy() {
        return this.dy;
    }

    /**
     * Adds another vector to this one, creating a new vector.
     * This operation represents combining two movements or forces.
     *
     * @param other The vector to add to this one.
     * @return A new Vector representing the sum of the two vectors.
     */
    public Vector add(Vector other) {
        return new Vector(this.dx + other.dx, this.dy + other.dy);
    }

    /**
     * Subtracts another vector from this one, creating a new vector.
     * This operation is useful for calculating relative movements or
     * differences between vectors.
     *
     * @param other The vector to subtract from this one.
     * @return A new Vector representing the difference between the two vectors.
     */
    public Vector subtract(Vector other) {
        return new Vector(this.dx - other.dx, this.dy - other.dy);
    }

    /**
     * Checks if this vector represents no movement (zero vector).
     * A zero vector has both components equal to zero and represents
     * a stationary state.
     *
     * @return true if this is a zero vector, false otherwise.
     */
    public boolean isZero() {
        return this.dx == 0 && this.dy == 0;
    }

    /**
     * Gets the maximum speed component of this vector.
     * This is calculated as the maximum absolute value between the
     * horizontal and vertical components, useful for game physics calculations.
     *
     * @return The maximum absolute value between dx and dy.
     */
    public int getMaxSpeed() {
        return Math.max(Math.abs(this.dx), Math.abs(this.dy));
    }

    @Override
    public String toString() {
        return "(" + this.dx + ", " + this.dy + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Vector vector = (Vector) o;
        return this.dx == vector.dx && this.dy == vector.dy;
    }

    @Override
    public int hashCode() {
        return 31 * this.dx + this.dy;
    }
}
