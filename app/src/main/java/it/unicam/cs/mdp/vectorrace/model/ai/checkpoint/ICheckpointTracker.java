package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Defines the interface for tracking checkpoints passed by players in the Vector Race game.
 * Implementations of this interface are responsible for storing and retrieving
 * information about which checkpoints have been reached by each player.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Recording checkpoint passage for each player</li>
 *   <li>Checking if a player has passed a specific checkpoint</li>
 * </ul>
 */
public interface ICheckpointTracker {
    /**
     * Checks if a player has passed a specific checkpoint.
     *
     * @param playerName The name of the player.
     * @param checkpoint The position of the checkpoint.
     * @return true if the player has passed the checkpoint, false otherwise.
     */
    boolean hasPassedCheckpoint(String playerName, Position checkpoint);

    /**
     * Marks a checkpoint as passed for a player.
     *
     * @param playerName The name of the player.
     * @param checkpoint The position of the checkpoint.
     */
    void markCheckpointAsPassed(String playerName, Position checkpoint);
}