package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import java.util.*;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Manages the state of the Breadth-First Search (BFS) algorithm.
 * This class maintains the queue of nodes to visit, the set of visited nodes,
 * and the starting node, ensuring efficient and correct execution of the BFS.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Queue management for node exploration</li>
 *   <li>Visited node tracking to prevent cycles</li>
 *   <li>Start node storage for path reconstruction</li>
 * </ul>
 */
public class BFSStateManager {
    private final Queue<BFSNode> queue;
    private final Set<BFSNode> visited;
    private final BFSNode startNode;

    /**
     * Initializes a new BFS state.
     *
     * @param startPosition The initial position for the search.
     * @param startVelocity The initial velocity for the search.
     */
    public BFSStateManager(Position startPosition, Vector startVelocity) {
        this.queue = new LinkedList<>();
        this.visited = new HashSet<>();
        this.startNode = new BFSNode(startPosition, startVelocity, null, null);

        this.queue.add(startNode);
        this.visited.add(startNode);
    }

    /**
     * Gets the next node to process from the queue.
     *
     * @return The next node to process, or null if the queue is empty.
     */
    public BFSNode getNextNode() {
        return queue.poll();
    }

    /**
     * Checks if there are any more nodes to process.
     *
     * @return true if the queue is empty, false otherwise.
     */
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * Adds a new node to the queue if it hasn't already been visited.
     *
     * @param node The node to add.
     * @return true if the node was added, false if it was already visited.
     */
    public boolean addNode(BFSNode node) {
        if (!visited.contains(node)) {
            visited.add(node);
            queue.add(node);
            return true;
        }
        return false;
    }

    /**
     * Gets the starting node of the search.
     *
     * @return The starting node.
     */
    public BFSNode getStartNode() {
        return startNode;
    }

    /**
     * Checks if a node has already been visited.
     *
     * @param node The node to check.
     * @return true if the node has been visited, false otherwise.
     */
    public boolean isVisited(BFSNode node) {
        return visited.contains(node);
    }
}