package it.unicam.cs.mdp.vectorrace.model.core;

/**
 * Rappresenta un vettore bidimensionale utilizzato per velocità e
 * accelerazione.
 */
public class Vector {

    private final int dx;
    private final int dy;
    public static final Vector ZERO = new Vector(0, 0);

    /**
     * Costruttore.
     *
     * @param dx componente orizzontale
     * @param dy componente verticale
     */
    public Vector(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return this.dx;
    }

    public int getDy() {
        return this.dy;
    }

    /**
     * Restituisce la somma di questo vettore con un altro.
     *
     * @param other altro vettore
     * @return nuovo vettore somma
     */
    public Vector add(Vector other) {
        return new Vector(this.dx + other.dx, this.dy + other.dy);
    }

    /**
     * Restituisce la differenza tra questo vettore e un altro.
     *
     * @param other altro vettore
     * @return nuovo vettore differenza
     */
    public Vector subtract(Vector other) {
        return new Vector(this.dx - other.dx, this.dy - other.dy);
    }

    /**
     * Verifica se il vettore è nullo (entrambe le componenti sono zero).
     *
     * @return true se il vettore è nullo, false altrimenti
     */
    public boolean isZero() {
        return this.dx == 0 && this.dy == 0;
    }

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
