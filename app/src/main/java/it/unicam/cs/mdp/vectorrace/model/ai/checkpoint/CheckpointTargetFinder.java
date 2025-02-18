package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.ai.services.FinishCellLocator;
import it.unicam.cs.mdp.vectorrace.model.ai.services.IFinishLocator;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Determines the next target cell (checkpoint or finish line) for a player.
 * This class uses specific strategies for checkpoint management and finish line
 * location to guide the AI player towards the goal.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Determining if the player should target the finish line</li>
 *   <li>Selecting the appropriate checkpoint based on the current level</li>
 *   <li>Managing player-specific target positions</li>
 * </ul>
 *
 * <p>Components:
 * <ul>
 *   <li>{@link ICheckpointMap} - Manages the map of checkpoints</li>
 *   <li>{@link IFinishLocator} - Locates the finish line cell</li>
 *   <li>{@link ICheckpointStrategy} - Selects the next checkpoint</li>
 * </ul>
 */
public class CheckpointTargetFinder {

    private final ICheckpointMap checkpointMap;
    private final IFinishLocator finishLocator;
    private final ICheckpointStrategy checkpointStrategy;
    private final Map<String, Position> playerTargets;

    /**
     * Creates a new CheckpointTargetFinder with specified strategies.
     *
     * @param checkpointMap The checkpoint map manager.
     * @param finishLocator The finish line locator.
     * @param checkpointStrategy The checkpoint selection strategy.
     */
    public CheckpointTargetFinder(
            ICheckpointMap checkpointMap,
            IFinishLocator finishLocator,
            ICheckpointStrategy checkpointStrategy) {
        this.checkpointMap = checkpointMap;
        this.finishLocator = finishLocator;
        this.checkpointStrategy = checkpointStrategy;
        this.playerTargets = new HashMap<>();
    }

    /**
     * Creates a default CheckpointTargetFinder using standard implementations.
     */
    public CheckpointTargetFinder() {
        this(new CheckpointMapManager(),
                new FinishCellLocator(),
                new NearestCheckpointStrategy());
    }

    /**
     * Finds the next target position (checkpoint or finish line) for the player.
     *
     * @param player The player for whom to find the target.
     * @param gameState The current game state.
     * @return The next target position.
     */
    public Position findNextTarget(Player player, GameState gameState) {
        Track track = gameState.getTrack();
        int currentCheckpointIndex = player.getNextCheckpointIndex();

        // Initialize or update the checkpoint map if necessary
        checkpointMap.initialize(track);

        // Check if we should target the finish line
        if (shouldTargetFinish(currentCheckpointIndex, track)) {
            return finishLocator.locateFinish(track);
        }

        // Get available checkpoints for the current level
        Set<Position> availableCheckpoints = checkpointMap.getCheckpoints(currentCheckpointIndex);

        // Select the appropriate checkpoint
        Position target = selectTarget(player, availableCheckpoints);
        return target != null ? target : finishLocator.locateFinish(track);
    }

    /**
     * Determines if the player should target the finish line.
     *
     * @param currentCheckpointIndex The player's current checkpoint index.
     * @param track The game track.
     * @return true if the player should target the finish line, false otherwise.
     */
    private boolean shouldTargetFinish(int currentCheckpointIndex, Track track) {
        return currentCheckpointIndex > track.getMaxCheckpoint() ||
                checkpointMap.getCheckpoints(currentCheckpointIndex).isEmpty();
    }

    /**
     * Selects the appropriate target from the available checkpoints.
     *
     * @param player The player for whom to select the target.
     * @param availableCheckpoints The set of available checkpoints.
     * @return The selected target position.
     */
    private Position selectTarget(Player player, Set<Position> availableCheckpoints) {
        // Check if the current target is still valid
        Position currentTarget = playerTargets.get(player.getName());
        if (currentTarget != null && availableCheckpoints.contains(currentTarget)) {
            return currentTarget;
        }

        // Select a new target using the configured strategy
        Position newTarget = checkpointStrategy.selectCheckpoint(
                availableCheckpoints,
                player.getPosition());

        // Store and return the new target
        if (newTarget != null) {
            playerTargets.put(player.getName(), newTarget);
        }
        return newTarget;
    }
}
