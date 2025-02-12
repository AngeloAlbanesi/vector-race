package it.unicam.cs.mdp.vectorrace.model.core;

import java.util.Objects;

/**
 * Rappresenta una posizione (coppia di coordinate intere) sul circuito.
 */
public class Position {

    private final int x;
    private final int y;

    /**
     * Costruttore.
     *
     * @param x coordinata X
     * @param y coordinata Y
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    /**
     * Calcola la distanza euclidea da un'altra posizione
     *
     * @param other altra posizione
     * @return distanza tra le due posizioni
     */
    public double distanceTo(Position other) {
        int dx = other.getX() - this.x;
        int dy = other.getY() - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Restituisce una nuova posizione ottenuta sommando il vettore corrente.
     *
     * @param vector vettore da aggiungere
     * @return nuova posizione
     */
    public Position add(Vector vector) {
        return new Position(x + vector.getDx(), y + vector.getDy());
    }

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
