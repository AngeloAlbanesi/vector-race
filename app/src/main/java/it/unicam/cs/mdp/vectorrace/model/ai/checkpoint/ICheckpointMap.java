package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import java.util.Set;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;

/**
 * Defines the operations for managing the checkpoint map in the Vector Race game.
 * Implementations of this interface are responsible for organizing and
 * providing access to checkpoint positions on the track.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Initializing the checkpoint map from the track layout</li>
 *   <li>Providing access to checkpoints by level or sequence number</li>
 * </ul>
 */
public interface ICheckpointMap {
    /**
     * Initializes the checkpoint map for the specified track.
     * This method should scan the track and store all checkpoint positions
     * for later retrieval.
     *
     * @param track The track from which to extract checkpoints.
     */
    void initialize(Track track);

    /**
     * Gets the set of checkpoint positions for the specified level.
     *
     * @param level The level or sequence number of the checkpoints to retrieve.
     * @return A set of checkpoint positions for that level, which may be empty
     *         if no checkpoints exist at that level.
     */
    Set<Position> getCheckpoints(int level);
}