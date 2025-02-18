package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import it.unicam.cs.mdp.vectorrace.model.ai.services.IMoveValidator;
import it.unicam.cs.mdp.vectorrace.model.core.AccelerationType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Executes the Breadth-First Search (BFS) algorithm to find a path.
 * This class manages the search process, exploring the game space and
 * delegating state management to the {@link BFSStateManager}.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>BFS search execution</li>
 *   <li>State management delegation</li>
 *   <li>Neighbor node processing</li>
 *   <li>Path reconstruction</li>
 * </ul>
 */
public class BFSExecutor {
    private static final int MAX_SPEED = 4;
    private final IMoveValidator moveValidator;

    /**
     * Creates a new BFSExecutor with the specified move validator.
     *
     * @param moveValidator The move validator to use for pathfinding.
     */
    public BFSExecutor(IMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    /**
     * Executes the BFS search to find a path to the target.
     *
     * @param start The starting position.
     * @param startVelocity The starting velocity.
     * @param target The target position.
     * @param track The track to search within.
     * @return A BFSSearchResult containing the next acceleration vector and a flag
     *         indicating if a path was found.
     */
    public BFSSearchResult search(Position start, Vector startVelocity, Position target, Track track) {
        BFSStateManager stateManager = new BFSStateManager(start, startVelocity);

        while (!stateManager.isQueueEmpty()) {
            BFSNode currentNode = stateManager.getNextNode();

            if (isTargetReached(currentNode, target)) {
                return reconstructPath(currentNode, stateManager.getStartNode());
            }

            processNeighbors(currentNode, stateManager, track);
        }

        return new BFSSearchResult(new Vector(0, 0), false);
    }

    /**
     * Checks if the current node has reached the target position.
     *
     * @param node The current node.
     * @param target The target position.
     * @return true if the node's position matches the target, false otherwise.
     */
    private boolean isTargetReached(BFSNode node, Position target) {
        return node.getPosition().equals(target);
    }

    /**
     * Processes all neighbor nodes of the current node.
     * Generates valid neighbor nodes by applying all possible acceleration
     * vectors and adding them to the state manager.
     *
     * @param currentNode The current node.
     * @param stateManager The state manager for BFS.
     * @param track The track to validate moves against.
     */
    private void processNeighbors(BFSNode currentNode, BFSStateManager stateManager, Track track) {
        for (Vector acceleration : AccelerationType.getAllVectors()) {
            BFSNode neighbor = generateNeighbor(currentNode, acceleration);

            if (neighbor != null && isValidMove(neighbor, track)) {
                stateManager.addNode(neighbor);
            }
        }
    }

    /**
     * Generates a new neighbor node by applying the given acceleration.
     * Returns null if the resulting velocity exceeds the maximum speed limit.
     *
     * @param currentNode The current node.
     * @param acceleration The acceleration vector to apply.
     * @return The new neighbor node, or null if the velocity is invalid.
     */
    private BFSNode generateNeighbor(BFSNode currentNode, Vector acceleration) {
        Vector newVelocity = currentNode.getVelocity().add(acceleration);

        if (Math.abs(newVelocity.getDx()) > MAX_SPEED ||
                Math.abs(newVelocity.getDy()) > MAX_SPEED) {
            return null;
        }

        Position newPosition = currentNode.getPosition().move(newVelocity);
        return new BFSNode(newPosition, newVelocity, currentNode, acceleration);
    }

    /**
     * Checks if a move is valid according to the move validator.
     *
     * @param node The node representing the move.
     * @param track The track to validate against.
     * @return true if the move is valid, false otherwise.
     */
    private boolean isValidMove(BFSNode node, Track track) {
        return moveValidator.validateTempMove(
                node.getParent().getPosition(),
                node.getVelocity(),
                track);
    }

    /**
     * Reconstructs the path from the goal node to the start node.
     *
     * @param goalNode The goal node.
     * @param startNode The start node.
     * @return A BFSSearchResult containing the next acceleration vector and a flag
     *         indicating if a path was found.
     */
    private BFSSearchResult reconstructPath(BFSNode goalNode, BFSNode startNode) {
        BFSNode current = goalNode;

        while (current.getParent() != null && current.getParent() != startNode) {
            current = current.getParent();
        }

        Vector acceleration = (current.getAccApplied() != null) ? current.getAccApplied() : new Vector(0, 0);

        return new BFSSearchResult(acceleration, true);
    }
}