package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import java.util.Comparator;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Implements the {@link ICheckpointStrategy} interface to select the checkpoint
 * nearest to the player's current position. This strategy uses Euclidean
 * distance to determine the closest checkpoint among the available options.
 *
 * <p>The nearest checkpoint is selected based solely on proximity,
 * without considering other factors such as path obstructions or
 * strategic advantages.
 */
public class NearestCheckpointStrategy implements ICheckpointStrategy {
    
    @Override
    public Position selectCheckpoint(Set<Position> checkpoints, Position currentPosition) {
        if (checkpoints == null || checkpoints.isEmpty() || currentPosition == null) {
            return null;
        }
        
        return checkpoints.stream()
            .min(Comparator.comparingDouble(cp -> cp.distanceTo(currentPosition)))
            .orElse(null);
    }
}