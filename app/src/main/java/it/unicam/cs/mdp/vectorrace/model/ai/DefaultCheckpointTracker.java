package it.unicam.cs.mdp.vectorrace.model.ai;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementazione predefinita dell'interfaccia ICheckpointTracker.
 * Gestisce il tracciamento dei checkpoint attraversati dai giocatori.
 */
public class DefaultCheckpointTracker implements ICheckpointTracker {
    private final Map<String, Set<Position>> passedCheckpoints;

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