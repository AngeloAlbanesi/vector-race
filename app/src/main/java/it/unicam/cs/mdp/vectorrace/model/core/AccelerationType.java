package it.unicam.cs.mdp.vectorrace.model.core;

import java.util.Arrays;

/**
 * Rappresenta le possibili accelerazioni nel gioco Vector Race.
 * Ogni accelerazione è rappresentata da un vettore (dx, dy) con valori in {-1, 0, 1}.
 */
public enum AccelerationType {
    NORTH_WEST(new Vector(-1, -1)),
    NORTH(new Vector(-1, 0)),
    NORTH_EAST(new Vector(-1, 1)),
    WEST(new Vector(0, -1)),
    NONE(new Vector(0, 0)),
    EAST(new Vector(0, 1)),
    SOUTH_WEST(new Vector(1, -1)),
    SOUTH(new Vector(1, 0)),
    SOUTH_EAST(new Vector(1, 1));

    private final Vector acceleration;

    AccelerationType(Vector acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Restituisce il vettore di accelerazione associato.
     * @return il vettore di accelerazione
     */
    public Vector getVector() {
        return acceleration;
    }

    /**
     * Restituisce tutti i vettori di accelerazione possibili.
     * @return array contenente tutti i vettori di accelerazione
     */
    public static Vector[] getAllVectors() {
        return Arrays.stream(values())
                    .map(AccelerationType::getVector)
                    .toArray(Vector[]::new);
    }
}