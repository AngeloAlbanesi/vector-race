package it.unicam.cs.mdp.vectorrace.model.ai;

import java.util.Objects;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

public class VectorRacePathState implements Comparable<VectorRacePathState> {
    private final Position position;
    private final Vector velocity;
    private double g;
    private double h;
    private final VectorRacePathState parent;

    public VectorRacePathState(Position position, Vector velocity, VectorRacePathState parent) {
        this.position = position;
        this.velocity = velocity;
        this.parent = parent;
    }

    public Position getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public VectorRacePathState getParent() {
        return parent;
    }

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