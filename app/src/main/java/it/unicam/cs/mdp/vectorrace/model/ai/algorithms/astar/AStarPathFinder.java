package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar;

import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointManager;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.IPathFinder;
import it.unicam.cs.mdp.vectorrace.model.core.AccelerationType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implements the A* search algorithm for pathfinding in the Vector Race game.
 * This class uses a configurable heuristic function and manages movement
 * validation to find an optimal path for AI players.
 *
 * <p>
 * Key features:
 * <ul>
 * <li>Heuristic-based pathfinding for efficiency</li>
 * <li>Move validation to ensure legal movements</li>
 * <li>Checkpoint management for tracking progress</li>
 * <li>Limited expansion count to prevent infinite loops</li>
 * </ul>
 *
 * <p>
 * The A* algorithm balances exploration and exploitation using the fCost,
 * which combines the actual cost from the start (gCost) and an estimated cost
 * to the goal (hCost).
 */
public class AStarPathFinder implements IPathFinder {

    private static final int MAX_SPEED = 5;
    private static final int MAX_EXPANSIONS = 200000;
    private static final double PENALTY_FACTOR = 5.0;

    private final IHeuristicCalculator heuristic;
    private final MovementManager movementManager;
    private final CheckpointManager checkpointManager;

    /**
     * Creates a new AStarPathFinder with specified components.
     *
     * @param heuristic         The heuristic function to estimate the cost to the
     *                          goal.
     * @param movementManager   The movement manager for validating moves.
     * @param checkpointManager The checkpoint manager for tracking progress.
     */
    public AStarPathFinder(
            IHeuristicCalculator heuristic,
            MovementManager movementManager,
            CheckpointManager checkpointManager) {
        this.heuristic = heuristic;
        this.movementManager = movementManager;
        this.checkpointManager = checkpointManager;
    }

    @Override
    public Vector findPath(Player player, GameState gameState, Position target) {
        if (target == null) {
            return new Vector(0, 0);
        }

        Position currentPos = player.getPosition();
        Vector currentVel = player.getVelocity();

        AStarNode startNode = initializeStartNode(currentPos, currentVel, target);
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(AStarNode::getFCost));
        Set<AStarNode> closedSet = new HashSet<>();

        openSet.add(startNode);
        AStarNode goalNode = findGoalNode(openSet, closedSet, target, gameState);

        return reconstructPath(goalNode, startNode, player, currentPos, gameState);
    }

    /**
     * Initializes the starting node for the A* search.
     *
     * @param currentPos The player's current position.
     * @param currentVel The player's current velocity.
     * @param target     The target position.
     * @return The initialized AStarNode.
     */
    private AStarNode initializeStartNode(
            Position currentPos,
            Vector currentVel,
            Position target) {
        AStarNode startNode = new AStarNode(currentPos, currentVel, null, null);
        startNode.setGCost(0.0);
        startNode.setHCost(
                calculateHeuristic(null, currentPos, currentVel, target));
        return startNode;
    }

    /**
     * Finds the goal node using the A* search algorithm.
     *
     * @param openSet   The priority queue of nodes to evaluate.
     * @param closedSet The set of nodes already evaluated.
     * @param target    The target position.
     * @param gameState The current game state.
     * @return The goal node if found, null otherwise.
     */
    private AStarNode findGoalNode(
            PriorityQueue<AStarNode> openSet,
            Set<AStarNode> closedSet,
            Position target,
            GameState gameState) {
        int expansionsCount = 0;
        while (!openSet.isEmpty()) {
            if (expansionsCount++ > MAX_EXPANSIONS) {
                return null;
            }

            AStarNode current = openSet.poll();
            if (current == null)
                break;

            if (closedSet.contains(current)) {
                continue;
            }
            closedSet.add(current);

            if (current.getPosition().equals(target)) {
                return current;
            }

            expandNode(current, openSet, closedSet, target, gameState);
        }
        return null;
    }

    /**
     * Expands a node by considering all possible acceleration vectors.
     *
     * @param current   The node to expand.
     * @param openSet   The priority queue of nodes to evaluate.
     * @param closedSet The set of nodes already evaluated.
     * @param target    The target position.
     * @param gameState The current game state.
     */
    private void expandNode(
            AStarNode current,
            PriorityQueue<AStarNode> openSet,
            Set<AStarNode> closedSet,
            Position target,
            GameState gameState) {
        for (Vector acc : AccelerationType.getAllVectors()) {
            Vector newVel = current.getVelocity().add(acc);
            if (!isValidVelocity(newVel)) {
                continue;
            }

            Position newPos = current.getPosition().move(newVel);
            if (!this.movementManager.validateMoveTemp(
                    current.getPosition(),
                    newVel,
                    gameState.getTrack())) {
                continue;
            }

            AStarNode neighbor = new AStarNode(newPos, newVel, current, acc);
            double tentativeG = current.getGCost() + 1.0;

            if (!closedSet.contains(neighbor) && tentativeG < neighbor.getGCost()) {
                updateNeighborCosts(current, neighbor, tentativeG, target);
                openSet.add(neighbor);
            }
        }
    }

    /**
     * Checks if a velocity vector is valid (within maximum speed limits).
     *
     * @param velocity The velocity vector to check.
     * @return true if the velocity is valid, false otherwise.
     */
    private boolean isValidVelocity(Vector velocity) {
        return (Math.abs(velocity.getDx()) <= MAX_SPEED &&
                Math.abs(velocity.getDy()) <= MAX_SPEED);
    }

    /**
     * Updates the costs of a neighbor node.
     *
     * @param current  The current node.
     * @param neighbor The neighbor node to update.
     * @param gCost    The cost from the start node to the neighbor.
     * @param target   The target position.
     */
    private void updateNeighborCosts(
            AStarNode current,
            AStarNode neighbor,
            double gCost,
            Position target) {
        neighbor.setGCost(gCost);
        neighbor.setHCost(
                calculateHeuristic(
                        current.getPosition(),
                        neighbor.getPosition(),
                        neighbor.getVelocity(),
                        target));
    }

    /**
     * Calculates the heuristic estimate from a position to the target.
     *
     * @param oldPos The previous position (used for penalty calculation).
     * @param newPos The new position.
     * @param newVel The new velocity.
     * @param target The target position.
     * @return The heuristic estimate.
     */
    private double calculateHeuristic(
            Position oldPos,
            Position newPos,
            Vector newVel,
            Position target) {
        double newDist = this.heuristic.calculate(newPos, target);

        // Add penalty if moving away from the target
        if (oldPos != null) {
            double oldDist = this.heuristic.calculate(oldPos, target);
            if (newDist > oldDist) {
                newDist += PENALTY_FACTOR;
            }
        }

        // Add velocity factor to favor smoother movements
        return (newDist + 0.5 * (Math.abs(newVel.getDx()) + Math.abs(newVel.getDy())));
    }

    /**
     * Reconstructs the path from the goal node to the start node and returns
     * the first acceleration vector in the path.
     *
     * @param goalNode   The goal node.
     * @param startNode  The start node.
     * @param player     The player for whom the path is being found.
     * @param currentPos The player's current position.
     * @param gameState  The current game state.
     * @return The first acceleration vector in the path, or a zero vector if no
     *         path is found.
     */
    private Vector reconstructPath(
            AStarNode goalNode,
            AStarNode startNode,
            Player player,
            Position currentPos,
            GameState gameState) {
        if (goalNode == null) {
            return new Vector(0, 0);
        }

        AStarNode cur = goalNode;
        while (cur.getParent() != null && cur.getParent() != startNode) {
            cur = cur.getParent();
        }

        Vector chosenAcc = cur.getAppliedAcceleration() != null
                ? cur.getAppliedAcceleration()
                : new Vector(0, 0);

        if (validateAndUpdatePath(player, chosenAcc, currentPos, gameState)) {
            return chosenAcc;
        }

        return new Vector(0, 0);
    }

    /**
     * Validates the final move and updates checkpoint status.
     *
     * @param player       The player making the move.
     * @param acceleration The acceleration vector for the move.
     * @param currentPos   The player's current position.
     * @param gameState    The current game state.
     * @return true if the move is valid, false otherwise.
     */
    private boolean validateAndUpdatePath(
            Player player,
            Vector acceleration,
            Position currentPos,
            GameState gameState) {
        if (!this.movementManager.validateMove(player, acceleration, gameState)) {
            return false;
        }

        Vector finalVelocity = player.getVelocity().add(acceleration);
        Position finalPos = currentPos.move(finalVelocity);
        this.checkpointManager.checkCrossedCheckpoints(
                player,
                currentPos,
                finalPos,
                gameState.getTrack());

        return true;
    }
}
