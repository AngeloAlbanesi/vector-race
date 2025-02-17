package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import java.util.*;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Gestisce lo stato della ricerca BFS, mantenendo la coda dei nodi da visitare,
 * l'insieme dei nodi visitati e il nodo di partenza.
 */
public class BFSStateManager {
    private final Queue<BFSNode> queue;
    private final Set<BFSNode> visited;
    private final BFSNode startNode;

    /**
     * Inizializza un nuovo stato della ricerca BFS.
     *
     * @param startPosition posizione iniziale
     * @param startVelocity velocità iniziale
     */
    public BFSStateManager(Position startPosition, Vector startVelocity) {
        this.queue = new LinkedList<>();
        this.visited = new HashSet<>();
        this.startNode = new BFSNode(startPosition, startVelocity, null, null);

        this.queue.add(startNode);
        this.visited.add(startNode);
    }

    /**
     * @return il prossimo nodo da processare
     */
    public BFSNode getNextNode() {
        return queue.poll();
    }

    /**
     * @return true se non ci sono più nodi da processare
     */
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * Aggiunge un nuovo nodo alla coda se non è già stato visitato.
     *
     * @param node il nodo da aggiungere
     * @return true se il nodo è stato aggiunto
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
     * @return il nodo di partenza
     */
    public BFSNode getStartNode() {
        return startNode;
    }

    /**
     * Verifica se un nodo è già stato visitato.
     *
     * @param node il nodo da verificare
     * @return true se il nodo è già stato visitato
     */
    public boolean isVisited(BFSNode node) {
        return visited.contains(node);
    }
}