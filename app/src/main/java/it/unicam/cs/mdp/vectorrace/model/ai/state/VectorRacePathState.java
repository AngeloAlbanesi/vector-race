package it.unicam.cs.mdp.vectorrace.model.ai.state;

import java.util.Objects;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Represents the state of a path in the Vector Race game, used for pathfinding algorithms.
 * This class encapsulates the position, velocity, and cost metrics associated with a
 * particular state in the search space.
 *
 * <p>Key attributes:
 * <ul>
 *   <li>position - The coordinates of the state on the track</li>
 *   <li>velocity - The velocity vector at this state</li>
 *   <li>g - The cost from the start state to this state</li>
 *   <li>h - The estimated cost from this state to the goal</li>
 *   <li>parent - The previous state in the path</li>
 * </ul>
 *
 * <p>This class implements the {@link Comparable} interface to allow for
 * efficient sorting and prioritization of path states during search.
 */
public class VectorRacePathState implements Comparable<VectorRacePathState> {
    private final Position position;
    private final Vector velocity;
    private double g;
    private double h;
    private final VectorRacePathState parent;

    /**
     * Constructs a new VectorRacePathState.
     *
     * @param position The position of this state.
     * @param velocity The velocity at this state.
     * @param parent The parent state in the path.
     */
    public VectorRacePathState(Position position, Vector velocity, VectorRacePathState parent) {
        this.position = position;
        this.velocity = velocity;
        this.parent = parent;
    }

    /**
     * Gets the position of this state.
     *
     * @return The position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the velocity at this state.
     *
     * @return The velocity.
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Gets the cost from the start state to this state.
     *
     * @return The g cost.
     */
    public double getG() {
        return g;
    }

    /**
     * Sets the cost from the start state to this state.
     *
     * @param g The g cost to set.
     */
    public void setG(double g) {
        this.g = g;
    }

    /**
     * Gets the estimated cost from this state to the goal.
     *
     * @return The h cost.
     */
    public double getH() {
        return h;
    }

    /**
     * Sets the estimated cost from this state to the goal.
     *
     * @param h The h cost to set.
     */
    public void setH(double h) {
        this.h = h;
    }

    /**
     * Gets the parent state in the path.
     *
     * @return The parent state.
     */
    public VectorRacePathState getParent() {
        return parent;
    }

    /**
     * Gets the total estimated cost (f = g + h).
     *
     * @return The total estimated cost.
     */
    public double getF() {
        return g + h;
    }

    @Override
    public int compareTo(VectorRacePathState other) {
        return Double.compare(this.getF(), other.getF());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VectorRacePathState that = (VectorRacePathState) o;
        return Objects.equals(position, that.position) &&
                Objects.equals(velocity, that.velocity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, velocity);
    }
}