package it.unicam.cs.mdp.vectorrace.model.ai.strategies.AStar;

import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar.AStarPathFinder;
import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar.ChebyshevHeuristic;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointManager;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointTargetFinder;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.IPathFinder;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Implements the A* search strategy for bot player movement.
 * This strategy uses a pure A* algorithm without any penalties or fallbacks,
 * focusing on finding the optimal path based on a heuristic estimate.
 *
 * <p>Key components:
 * <ul>
 *   <li>{@link AStarPathFinder} - Executes the A* search algorithm</li>
 *   <li>{@link ChebyshevHeuristic} - Provides heuristic estimates for path cost</li>
 *   <li>{@link CheckpointTargetFinder} - Determines the next target position</li>
 * </ul>
 *
 * <p>The A* strategy uses a heuristic to guide its search, typically resulting
 * in more efficient pathfinding compared to uninformed search algorithms like BFS.
 */
public class PureAStarStrategy implements AIStrategy {
    private final IPathFinder pathFinder;
    private final CheckpointTargetFinder targetFinder;

    /**
     * Creates a new PureAStarStrategy.
     * Initializes all necessary dependencies with default implementations:
     * <ul>
     *   <li>Chebyshev Heuristic for heuristic estimates</li>
     *   <li>Movement Manager for move validation</li>
     *   <li>Checkpoint Manager for tracking checkpoints</li>
     * </ul>
     */
    public PureAStarStrategy() {
        this.targetFinder = new CheckpointTargetFinder();
        this.pathFinder = new AStarPathFinder(
                new ChebyshevHeuristic(),
                new MovementManager(),
                new CheckpointManager());
    }

    /**
     * Creates a new PureAStarStrategy with custom path finder and target finder.
     * This constructor allows for dependency injection, enabling the use of
     * custom implementations for pathfinding and target selection.
     *
     * @param pathFinder The pathfinding algorithm to use.
     * @param targetFinder The component for finding the next target position.
     */
    public PureAStarStrategy(IPathFinder pathFinder, CheckpointTargetFinder targetFinder) {
        this.pathFinder = pathFinder;
        this.targetFinder = targetFinder;
    }

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        Position target = targetFinder.findNextTarget(player, gameState);
        if (target == null) {
            return new Vector(0, 0);
        }

        return pathFinder.findPath(player, gameState, target);
    }
}
