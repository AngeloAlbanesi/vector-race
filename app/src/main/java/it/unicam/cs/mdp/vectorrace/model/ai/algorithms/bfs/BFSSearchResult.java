package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Classe che rappresenta il risultato di una ricerca BFS.
 * Contiene l'accelerazione successiva da applicare e un flag che indica se il target è stato trovato.
 */
public class BFSSearchResult {
    private final Vector nextAcceleration;
    private final boolean found;

    public BFSSearchResult(Vector nextAcceleration, boolean found) {
        this.nextAcceleration = nextAcceleration;
        this.found = found;
    }

    public Vector getNextAcceleration() {
        return nextAcceleration;
    }

    public boolean isFound() {
        return found;
    }
}