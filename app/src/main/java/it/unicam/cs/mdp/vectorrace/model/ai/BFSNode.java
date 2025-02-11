package it.unicam.cs.mdp.vectorrace.model.ai;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Rappresenta un nodo nell'algoritmo BFS per il calcolo del percorso.
 * Immutable per garantire thread-safety e corretto funzionamento in collections.
 */
public class BFSNode {
    private final Position position;
    private final Vector velocity;
    private final BFSNode parent;
    private final Vector accApplied;

    public BFSNode(Position position, Vector velocity, BFSNode parent, Vector accApplied) {
        this.position = position;
        this.velocity = velocity;
        this.parent = parent;
        this.accApplied = accApplied;
    }

    public Position getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public BFSNode getParent() {
        return parent;
    }

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