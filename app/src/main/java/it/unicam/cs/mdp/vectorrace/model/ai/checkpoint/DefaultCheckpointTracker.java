package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of the {@link ICheckpointTracker} interface.
 * This class manages the tracking of checkpoints that have been passed by players
 * during the Vector Race game.
 *
 * <p>Key features:
 * <ul>
 *   <li>Stores passed checkpoints for each player</li>
 *   <li>Provides methods to check and mark checkpoint passage</li>
 *   <li>Uses a simple in-memory data structure for tracking</li>
 * </ul>
 */
public class DefaultCheckpointTracker implements ICheckpointTracker {
    private final Map<String, Set<Position>> passedCheckpoints;

    /**
     * Creates a new DefaultCheckpointTracker.
     * Initializes the data structures used to track passed checkpoints.
     */
    public DefaultCheckpointTracker() {
        this.passedCheckpoints = new HashMap<>();
    }

    @Override
    public boolean hasPassedCheckpoint(String playerName, Position checkpoint) {
        return passedCheckpoints.getOrDefault(playerName, new HashSet<>())
                .contains(checkpoint);
    }

    @Override
    public void markCheckpointAsPassed(String playerName, Position checkpoint) {
        passedCheckpoints.computeIfAbsent(playerName, k -> new HashSet<>())
                .add(checkpoint);
    }
}