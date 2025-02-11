package it.unicam.cs.mdp.vectorrace.model.ai;

import java.util.Comparator;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Implementazione dell'interfaccia ICheckpointStrategy che seleziona il checkpoint
 * più vicino alla posizione corrente del giocatore usando la distanza euclidea.
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