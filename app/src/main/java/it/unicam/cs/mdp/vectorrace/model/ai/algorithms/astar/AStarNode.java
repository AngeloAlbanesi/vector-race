package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Represents a node in the A* search algorithm.
 * This class encapsulates the state information for a particular point
 * in the search space, including position, velocity, parent node,
 * and associated costs (gCost, hCost, fCost).
 *
 * <p>Key attributes:
 * <ul>
 *   <li>Position - The coordinates of the node on the track</li>
 *   <li>Velocity - The velocity vector at this node</li>
 *   <li>Parent - The node from which this node was reached</li>
 *   <li>gCost - The cost from the start node to this node</li>
 *   <li>hCost - The estimated cost from this node to the goal</li>
 *   <li>fCost - The total estimated cost (gCost + hCost)</li>
 * </ul>
 */
public class AStarNode {
    private final Position position;
    private final Vector velocity;
    private final AStarNode parent;
    private final Vector appliedAcceleration;

    // Costs for A*
    private double gCost = Double.POSITIVE_INFINITY; // accumulated cost
    private double hCost = 0.0; // heuristic estimate
    private double fCost = 0.0; // f = g + h

    /**
     * Constructs a new AStarNode.
     *
     * @param position The position of this node.
     * @param velocity The velocity at this node.
     * @param parent The parent node in the search tree.
     * @param acceleration The acceleration applied to reach this node.
     */
    public AStarNode(Position position, Vector velocity, AStarNode parent, Vector acceleration) {
        this.position = position;
        this.velocity = velocity;
        this.parent = parent;
        this.appliedAcceleration = acceleration;
    }

    /**
     * Gets the position of this node.
     *
     * @return The position.
     */
    public Position getPosition() {
        return this.position;
    }

    /**
     * Gets the velocity at this node.
     *
     * @return The velocity.
     */
    public Vector getVelocity() {
        return this.velocity;
    }

    /**
     * Gets the parent node.
     *
     * @return The parent node, or null if this is the start node.
     */
    public AStarNode getParent() {
        return this.parent;
    }

    /**
     * Gets the acceleration applied to reach this node from its parent.
     *
     * @return The applied acceleration vector.
     */
    public Vector getAppliedAcceleration() {
        return this.appliedAcceleration;
    }

    /**
     * Gets the cost from the start node to this node.
     *
     * @return The gCost.
     */
    public double getGCost() {
        return this.gCost;
    }

    /**
     * Sets the cost from the start node to this node.
     *
     * @param gCost The gCost to set.
     */
    public void setGCost(double gCost) {
        this.gCost = gCost;
        this.updateFCost();
    }

    /**
     * Gets the estimated cost from this node to the goal.
     *
     * @return The hCost.
     */
    public double getHCost() {
        return this.hCost;
    }

    /**
     * Sets the estimated cost from this node to the goal.
     *
     * @param hCost The hCost to set.
     */
    public void setHCost(double hCost) {
        this.hCost = hCost;
        this.updateFCost();
    }

    /**
     * Gets the total estimated cost (fCost = gCost + hCost).
     *
     * @return The fCost.
     */
    public double getFCost() {
        return this.fCost;
    }

    /**
     * Updates the total estimated cost (fCost) based on gCost and hCost.
     */
    private void updateFCost() {
        this.fCost = this.gCost + this.hCost;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AStarNode)) {
            return false;
        }
        AStarNode other = (AStarNode) o;
        return this.position.equals(other.position) &&
                this.velocity.equals(other.velocity);
    }

    @Override
    public int hashCode() {
        return this.position.hashCode() * 31 + this.velocity.hashCode();
    }
}