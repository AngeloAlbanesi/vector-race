package it.unicam.cs.mdp.vectorrace.model.ai;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Rappresenta un nodo nell'algoritmo A*.
 * Contiene la posizione, la velocità corrente, il nodo genitore e i costi
 * associati.
 */
public class AStarNode {
    private final Position position;
    private final Vector velocity;
    private final AStarNode parent;
    private final Vector appliedAcceleration;

    // Costi per A*
    private double gCost = Double.POSITIVE_INFINITY; // costo accumulato
    private double hCost = 0.0; // stima euristica
    private double fCost = 0.0; // f = g + h

    /**
     * Costruisce un nuovo nodo A*.
     *
     * @param position     la posizione corrente
     * @param velocity     la velocità corrente
     * @param parent       il nodo genitore
     * @param acceleration l'accelerazione applicata per raggiungere questo nodo
     */
    public AStarNode(Position position, Vector velocity, AStarNode parent, Vector acceleration) {
        this.position = position;
        this.velocity = velocity;
        this.parent = parent;
        this.appliedAcceleration = acceleration;
    }

    public Position getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public AStarNode getParent() {
        return parent;
    }

    public Vector getAppliedAcceleration() {
        return appliedAcceleration;
    }

    public double getGCost() {
        return gCost;
    }

    public void setGCost(double gCost) {
        this.gCost = gCost;
        updateFCost();
    }

    public double getHCost() {
        return hCost;
    }

    public void setHCost(double hCost) {
        this.hCost = hCost;
        updateFCost();
    }

    public double getFCost() {
        return fCost;
    }

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
        return position.hashCode() * 31 + velocity.hashCode();
    }
}