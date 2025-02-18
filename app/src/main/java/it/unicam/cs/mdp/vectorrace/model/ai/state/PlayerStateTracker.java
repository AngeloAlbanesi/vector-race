package it.unicam.cs.mdp.vectorrace.model.ai.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Tracks the state of a player during the Vector Race game.
 * This class is responsible for maintaining information about a player's
 * movement history, stuck status, and looping behavior to influence AI decisions.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Detecting when a player is stuck (not moving)</li>
 *   <li>Tracking player movement history to identify loops</li>
 *   <li>Storing the last movement direction</li>
 * </ul>
 */
public class PlayerStateTracker {
    private static final int STUCK_THRESHOLD = 3;
    private final Map<String, Integer> stuckCounter = new HashMap<>();
    private final Map<String, Vector> lastDirection = new HashMap<>();
    private final Map<String, Set<Position>> playerHistory = new HashMap<>();
    private final Map<String, Integer> loopCounter = new HashMap<>();

    /**
     * Checks if a player is stuck (not moving) based on their velocity.
     * If the player's velocity is zero for a certain number of turns
     * (defined by STUCK_THRESHOLD), they are considered stuck.
     *
     * @param playerName The name of the player.
     * @param velocity The player's current velocity.
     * @return true if the player is stuck, false otherwise.
     */
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

    /**
     * Updates the player's movement history with their current position.
     * This method also checks for looping behavior and resets the history
     * if a loop is detected.
     *
     * @param playerName The name of the player.
     * @param position The player's current position.
     */
    public void updatePlayerHistory(String playerName, Position position) {
        Set<Position> history = playerHistory.computeIfAbsent(playerName, k -> new HashSet<>());
        history.add(position);

        // Check for looping behavior
        if (history.size() > 5) {
            int loopCount = loopCounter.getOrDefault(playerName, 0);
            if (loopCount >= 3) {
                resetPlayerHistory(playerName);
            } else {
                loopCounter.put(playerName, loopCount + 1);
            }
        }
    }

    /**
     * Gets the last direction the player was moving in.
     *
     * @param playerName The name of the player.
     * @return The last direction vector, or null if no direction has been recorded.
     */
    public Vector getLastDirection(String playerName) {
        return lastDirection.get(playerName);
    }

    /**
     * Resets the player's movement history and loop counter.
     * This is called when looping behavior is detected to clear the history
     * and allow the AI to explore new paths.
     *
     * @param playerName The name of the player.
     */
    private void resetPlayerHistory(String playerName) {
        playerHistory.computeIfAbsent(playerName, k -> new HashSet<>()).clear();
        loopCounter.put(playerName, 0);
        lastDirection.remove(playerName);
    }
}