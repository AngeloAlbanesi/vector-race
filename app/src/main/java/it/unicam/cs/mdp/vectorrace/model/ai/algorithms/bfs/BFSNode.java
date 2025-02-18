package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Represents a node in the Breadth-First Search (BFS) algorithm.
 * This immutable class stores the state of a particular position and
 * velocity during the search process.
 *
 * <p>Key attributes:
 * <ul>
 *   <li>Position - The coordinates of the node on the track</li>
 *   <li>Velocity - The velocity vector at this node</li>
 *   <li>Parent - The node from which this node was reached</li>
 *   <li>accApplied - The acceleration applied to reach this node</li>
 * </ul>
 *
 * <p>Immutability ensures thread safety and correct behavior in collections.
 */
public class BFSNode {
    private final Position position;
    private final Vector velocity;
    private final BFSNode parent;
    private final Vector accApplied;

    /**
     * Constructs a new BFSNode.
     *
     * @param position The position of this node.
     * @param velocity The velocity at this node.
     * @param parent The parent node in the search tree.
     * @param accApplied The acceleration applied to reach this node.
     */
    public BFSNode(Position position, Vector velocity, BFSNode parent, Vector accApplied) {
        this.position = position;
        this.velocity = velocity;
        this.parent = parent;
        this.accApplied = accApplied;
    }

    /**
     * Gets the position of this node.
     *
     * @return The position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the velocity at this node.
     *
     * @return The velocity.
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Gets the parent node.
     *
     * @return The parent node, or null if this is the start node.
     */
    public BFSNode getParent() {
        return parent;
    }

    /**
     * Gets the acceleration applied to reach this node from its parent.
     *
     * @return The applied acceleration vector.
     */
    public Vector getAccApplied() {
        return accApplied;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BFSNode)) {
            return false;
        }
        BFSNode other = (BFSNode) o;
        return this.position.equals(other.position) && this.velocity.equals(other.velocity);
    }

    @Override
    public int hashCode() {
        return position.hashCode() * 31 + velocity.hashCode();
    }
}