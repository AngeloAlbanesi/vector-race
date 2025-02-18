package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Represents the result of a Breadth-First Search (BFS) operation.
 * This class encapsulates the outcome of the search, providing information
 * about the next acceleration vector to apply and whether the target was found.
 *
 * <p>Key attributes:
 * <ul>
 *   <li>nextAcceleration - The recommended acceleration vector for the next move</li>
 *   <li>found - A flag indicating whether the target was reached during the search</li>
 * </ul>
 */
public class BFSSearchResult {
    private final Vector nextAcceleration;
    private final boolean found;

    /**
     * Creates a new BFSSearchResult.
     *
     * @param nextAcceleration The recommended acceleration vector.
     * @param found A flag indicating whether the target was reached.
     */
    public BFSSearchResult(Vector nextAcceleration, boolean found) {
        this.nextAcceleration = nextAcceleration;
        this.found = found;
    }

    /**
     * Gets the recommended acceleration vector for the next move.
     *
     * @return The next acceleration vector.
     */
    public Vector getNextAcceleration() {
        return nextAcceleration;
    }

    /**
     * Checks if the target was reached during the search.
     *
     * @return true if the target was found, false otherwise.
     */
    public boolean isFound() {
        return found;
    }
}