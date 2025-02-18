package it.unicam.cs.mdp.vectorrace.model.ai.strategies.bfs;

import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs.BFSExecutor;
import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs.BFSSearchResult;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointManager;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointTargetFinder;
import it.unicam.cs.mdp.vectorrace.model.ai.services.IMoveValidator;
import it.unicam.cs.mdp.vectorrace.model.ai.services.MovementValidatorAdapter;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Implements the Breadth-First Search (BFS) strategy for AI player movement.
 * This strategy uses specialized components to manage different aspects of
 * the BFS algorithm, promoting modularity and testability.
 *
 * <p>Key components:
 * <ul>
 *   <li>{@link BFSExecutor} - Executes the BFS algorithm</li>
 *   <li>{@link IMoveValidator} - Validates potential moves</li>
 *   <li>{@link CheckpointTargetFinder} - Determines the next target position</li>
 *   <li>{@link CheckpointManager} - Manages checkpoint progression</li>
 * </ul>
 *
 * <p>The BFS strategy guarantees the shortest path in terms of moves,
 * systematically exploring all possible paths until the target is found.
 */
public class BFSStrategy implements AIStrategy {

    private final BFSExecutor bfsExecutor;
    private final IMoveValidator moveValidator;
    private final CheckpointTargetFinder targetFinder;
    private final CheckpointManager checkpointManager;

    /**
     * Creates a new BFSStrategy with dependency injection for all components.
     * This constructor allows for maximum flexibility in configuring the
     * strategy with custom implementations.
     *
     * @param bfsExecutor The BFS algorithm executor.
     * @param moveValidator The move validator.
     * @param targetFinder The checkpoint target finder.
     * @param checkpointManager The checkpoint manager.
     */
    public BFSStrategy(BFSExecutor bfsExecutor,
            IMoveValidator moveValidator,
            CheckpointTargetFinder targetFinder,
            CheckpointManager checkpointManager) {
        this.bfsExecutor = bfsExecutor;
        this.moveValidator = moveValidator;
        this.targetFinder = targetFinder;
        this.checkpointManager = checkpointManager;
    }

    /**
     * Creates a default BFSStrategy with standard component implementations.
     * This constructor provides a convenient way to create a BFS strategy
     * with default behavior.
     */
    public BFSStrategy() {
        this.moveValidator = new MovementValidatorAdapter();
        this.bfsExecutor = new BFSExecutor(moveValidator);
        this.targetFinder = new CheckpointTargetFinder();
        this.checkpointManager = new CheckpointManager();
    }

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        Position target = findTargetPosition(player, gameState);
        if (target == null) {
            return Vector.ZERO;
        }

        Vector acceleration = calculateAcceleration(player, target, gameState);
        return processCalculatedAcceleration(player, acceleration, gameState);
    }

    /**
     * Finds the next target position for the player.
     * Delegates the task to the {@link CheckpointTargetFinder}.
     *
     * @param player The player for whom to find the target.
     * @param gameState The current game state.
     * @return The next target position.
     */
    private Position findTargetPosition(Player player, GameState gameState) {
        return targetFinder.findNextTarget(player, gameState);
    }

    /**
     * Calculates the acceleration vector to reach the target position.
     * Executes the BFS algorithm to find the shortest path and determines
     * the next acceleration vector.
     *
     * @param player The player for whom to calculate the acceleration.
     * @param target The target position.
     * @param gameState The current game state.
     * @return The acceleration vector to move towards the target.
     */
    private Vector calculateAcceleration(Player player, Position target, GameState gameState) {
        BFSSearchResult result = bfsExecutor.search(
                player.getPosition(),
                player.getVelocity(),
                target,
                gameState.getTrack());

        return result.isFound() ? result.getNextAcceleration() : Vector.ZERO;
    }

    /**
     * Processes the calculated acceleration vector.
     * Validates the final move and updates checkpoint status.
     *
     * @param player The player for whom the acceleration was calculated.
     * @param acceleration The calculated acceleration vector.
     * @param gameState The current game state.
     * @return The validated acceleration vector, or Vector.ZERO if invalid.
     */
    private Vector processCalculatedAcceleration(Player player, Vector acceleration, GameState gameState) {
        return validateFinalMove(player, acceleration, player.getPosition(), gameState)
                ? acceleration
                : Vector.ZERO;
    }

    /**
     * Validates the final move and updates crossed checkpoints.
     * Checks if the final move is valid and updates the checkpoint manager
     * to mark any crossed checkpoints.
     *
     * @param player The player making the move.
     * @param acceleration The acceleration vector for the move.
     * @param startPosition The starting position of the move.
     * @param gameState The current game state.
     * @return true if the move is valid, false otherwise.
     */
    private boolean validateFinalMove(Player player, Vector acceleration,
            Position startPosition, GameState gameState) {
        Vector finalVelocity = player.getVelocity().add(acceleration);
        Position finalPosition = player.getPosition().move(finalVelocity);

        if (moveValidator.validateRealMove(player, acceleration, gameState)) {
            checkpointManager.checkCrossedCheckpoints(
                    player,
                    startPosition,
                    finalPosition,
                    gameState.getTrack());
            return true;
        }

        return false;
    }
}
