package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import it.unicam.cs.mdp.vectorrace.model.ai.services.IMoveValidator;
import it.unicam.cs.mdp.vectorrace.model.core.AccelerationType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Classe responsabile dell'esecuzione dell'algoritmo BFS.
 * Implementa la logica di ricerca delegando la gestione dello stato al BFSStateManager.
 */
public class BFSExecutor {
    private static final int MAX_SPEED = 4;
    private final IMoveValidator moveValidator;

    public BFSExecutor(IMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    /**
     * Esegue la ricerca BFS per trovare il percorso verso il target.
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
     * Verifica se il nodo corrente ha raggiunto il target.
     */
    private boolean isTargetReached(BFSNode node, Position target) {
        return node.getPosition().equals(target);
    }

    /**
     * Processa tutti i nodi vicini del nodo corrente.
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
    private BFSSearchResult reconstructPath(BFSNode goalNode, BFSNode startNode) {
        BFSNode current = goalNode;
        
        while (current.getParent() != null && current.getParent() != startNode) {
            current = current.getParent();
        }

        Vector acceleration = (current.getAccApplied() != null) ?
            current.getAccApplied() : new Vector(0, 0);
            
        return new BFSSearchResult(acceleration, true);
    }
}