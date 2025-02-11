package it.unicam.cs.mdp.vectorrace.model.ai.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

public class PlayerStateTracker {
    private static final int STUCK_THRESHOLD = 3;
    private final Map<String, Integer> stuckCounter = new HashMap<>();
    private final Map<String, Vector> lastDirection = new HashMap<>();
    private final Map<String, Set<Position>> playerHistory = new HashMap<>();
    private final Map<String, Integer> loopCounter = new HashMap<>();

    public boolean isPlayerStuck(String playerName, Vector velocity) {
        if (velocity.isZero()) {
            int stuck = stuckCounter.getOrDefault(playerName, 0) + 1;
            stuckCounter.put(playerName, stuck);
            return stuck >= STUCK_THRESHOLD;
        } else {
            stuckCounter.put(playerName, 0);
            if (!velocity.isZero()) {
                lastDirection.put(playerName, velocity);
            }
            return false;
        }
    }

    public void updatePlayerHistory(String playerName, Position position) {
        Set<Position> history = playerHistory.computeIfAbsent(playerName, k -> new HashSet<>());
        history.add(position);

        // Verifica loop
        if (history.size() > 5) {
            int loopCount = loopCounter.getOrDefault(playerName, 0);
            if (loopCount >= 3) {
                resetPlayerHistory(playerName);
            } else {
                loopCounter.put(playerName, loopCount + 1);
            }
        }
    }

    public Vector getLastDirection(String playerName) {
        return lastDirection.get(playerName);
    }

    private void resetPlayerHistory(String playerName) {
        playerHistory.computeIfAbsent(playerName, k -> new HashSet<>()).clear();
        loopCounter.put(playerName, 0);
        lastDirection.remove(playerName);
    }
}