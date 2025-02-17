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
 * Implementazione dell'algoritmo A* per la ricerca del percorso.
 * Utilizza una funzione euristica configurabile e gestisce la validazione dei
 * movimenti. Include una penalità ridotta quando ci si allontana dal target.
 */
public class AStarPathFinder implements IPathFinder {

    private static final int MAX_SPEED = 5;
    private static final int MAX_EXPANSIONS = 200000;
    private static final double PENALTY_FACTOR = 5.0;

    private final IHeuristicCalculator heuristic;
    private final MovementManager movementManager;
    private final CheckpointManager checkpointManager;

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

    private boolean isValidVelocity(Vector velocity) {
        return (Math.abs(velocity.getDx()) <= MAX_SPEED &&
                Math.abs(velocity.getDy()) <= MAX_SPEED);
    }

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

    private double calculateHeuristic(
            Position oldPos,
            Position newPos,
            Vector newVel,
            Position target) {
        double newDist = this.heuristic.calculate(newPos, target);

        // Aggiungi penalità se ci si allontana dal target
        if (oldPos != null) {
            double oldDist = this.heuristic.calculate(oldPos, target);
            if (newDist > oldDist) {
                newDist += PENALTY_FACTOR;
            }
        }

        // Aggiungi fattore velocità per favorire movimenti più fluidi
        return (newDist + 0.5 * (Math.abs(newVel.getDx()) + Math.abs(newVel.getDy())));
    }

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
