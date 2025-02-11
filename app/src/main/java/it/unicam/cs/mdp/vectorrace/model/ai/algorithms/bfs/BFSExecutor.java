package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import java.util.*;

import it.unicam.cs.mdp.vectorrace.model.ai.services.IMoveValidator;
import it.unicam.cs.mdp.vectorrace.model.core.AccelerationType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Classe responsabile dell'esecuzione dell'algoritmo BFS.
 * Separa la logica di ricerca dalla strategia principale.
 */
public class BFSExecutor {
    private static final int MAX_SPEED = 4;
    private final IMoveValidator moveValidator;

    public BFSExecutor(IMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    /**
     * Risultato della ricerca BFS
     */
    public static class SearchResult {
        private final Vector nextAcceleration;
        private final boolean found;

        public SearchResult(Vector nextAcceleration, boolean found) {
            this.nextAcceleration = nextAcceleration;
            this.found = found;
        }

        public Vector getNextAcceleration() {
            return nextAcceleration;
        }

        public boolean isFound() {
            return found;
        }
    }

    /**
     * Esegue la ricerca BFS per trovare il percorso verso il target.
     */
    public SearchResult search(Position start, Vector startVelocity, Position target, Track track) {
        SearchState state = initializeSearch(start, startVelocity);
        
        while (!state.queue.isEmpty()) {
            BFSNode currentNode = state.queue.poll();
            
            if (isTargetReached(currentNode, target)) {
                return reconstructPath(currentNode, state.startNode);
            }
            
            processNeighbors(currentNode, state, track);
        }
        
        return new SearchResult(new Vector(0, 0), false);
    }

    /**
     * Inizializza lo stato della ricerca.
     */
    private SearchState initializeSearch(Position start, Vector startVelocity) {
        Queue<BFSNode> queue = new LinkedList<>();
        Set<BFSNode> visited = new HashSet<>();
        BFSNode startNode = new BFSNode(start, startVelocity, null, null);
        
        queue.add(startNode);
        visited.add(startNode);
        
        return new SearchState(queue, visited, startNode);
    }

    /**
     * Verifica se il nodo corrente ha raggiunto il target.
     */
    private boolean isTargetReached(BFSNode node, Position target) {
        return node.getPosition().equals(target);
    }

    /**
     * Processa tutti i nodi vicini del nodo corrente.
     */
    private void processNeighbors(BFSNode currentNode, SearchState state, Track track) {
        for (Vector acceleration : AccelerationType.getAllVectors()) {
            BFSNode neighbor = generateNeighbor(currentNode, acceleration);
            
            if (neighbor != null && isValidMove(neighbor, track) && !state.visited.contains(neighbor)) {
                state.visited.add(neighbor);
                state.queue.add(neighbor);
            }
        }
    }

    /**
     * Genera un nuovo nodo vicino applicando l'accelerazione.
     * Ritorna null se il nodo non rispetta i limiti di velocità.
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
     * Verifica se la mossa è valida secondo il validatore.
     */
    private boolean isValidMove(BFSNode node, Track track) {
        return moveValidator.validateTempMove(
            node.getParent().getPosition(),
            node.getVelocity(),
            track
        );
    }

    /**
     * Ricostruisce il percorso dal nodo goal fino allo start.
     */
    private SearchResult reconstructPath(BFSNode goalNode, BFSNode startNode) {
        BFSNode current = goalNode;
        
        while (current.getParent() != null && current.getParent() != startNode) {
            current = current.getParent();
        }

        Vector acceleration = (current.getAccApplied() != null) ? 
            current.getAccApplied() : new Vector(0, 0);
            
        return new SearchResult(acceleration, true);
    }

    /**
     * Classe di supporto per mantenere lo stato della ricerca.
     */
    private static class SearchState {
        final Queue<BFSNode> queue;
        final Set<BFSNode> visited;
        final BFSNode startNode;

        SearchState(Queue<BFSNode> queue, Set<BFSNode> visited, BFSNode startNode) {
            this.queue = queue;
            this.visited = visited;
            this.startNode = startNode;
        }
    }
}