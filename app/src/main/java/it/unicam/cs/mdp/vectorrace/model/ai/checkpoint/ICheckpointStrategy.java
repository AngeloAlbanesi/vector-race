package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import java.util.Set;
import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Defines the strategy for selecting the most appropriate checkpoint
 * from a set of available checkpoints for a given level.
 * Implementations of this interface encapsulate different algorithms
 * for checkpoint selection, allowing for flexible AI behavior.
 *
 * <p>Implementations are responsible for:
 * <ul>
 *   <li>Analyzing available checkpoints</li>
 *   <li>Considering the player's current position</li>
 *   <li>Selecting the optimal checkpoint based on the strategy</li>
 * </ul>
 */
public interface ICheckpointStrategy {
    /**
     * Selects the most appropriate checkpoint from the available checkpoints,
     * based on the current position.
     *
     * @param checkpoints The set of available checkpoints.
     * @param currentPosition The current position from which to calculate the selection.
     * @return The selected checkpoint, or null if no checkpoint is available.
     */
    Position selectCheckpoint(Set<Position> checkpoints, Position currentPosition);
}