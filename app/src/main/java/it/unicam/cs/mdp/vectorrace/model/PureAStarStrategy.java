package it.unicam.cs.mdp.vectorrace.model;

import java.util.*;

/**
 * A* "puro" senza penalità e fallback.
 * Usa un closedSet e la funzione f = g + h per l'ordinamento.
 */
public class PureAStarStrategy implements AIStrategy {

    private final MovementManager movementManager = new MovementManager();
    private static final Vector[] ACCELERATIONS = {
            new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1),
            new Vector(0, -1),  new Vector(0, 0),  new Vector(0, 1),
            new Vector(1, -1),  new Vector(1, 0),  new Vector(1, 1)
    };
    private static final int MAX_SPEED = 7;

    private final CheckpointTargetFinder targetFinder = new CheckpointTargetFinder();
    private final CheckpointManager checkpointManager = new CheckpointManager();

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        Position currentPos = player.getPosition();
        Vector currentVel = player.getVelocity();

        // Trova il prossimo checkpoint (o finish) da raggiungere.
        Position target = targetFinder.findNextTarget(player, gameState);
        if (target == null) {
            // Non ci sono più checkpoint (o circuito caricato male), muoviti zero
            return new Vector(0, 0);
        }

        // A* standard: costruiamo un min-heap con f = g + h
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        // closedSet per non riespandere gli stessi (pos,vel) con costi peggiori
        Set<AStarNode> closedSet = new HashSet<>();

        // Nodo di partenza
        AStarNode startNode = new AStarNode(currentPos, currentVel, null, null);
        startNode.g = 0.0;
        startNode.h = chebyshevDist(currentPos, target);
        startNode.f = startNode.g + startNode.h;
        openSet.add(startNode);

        AStarNode goalNode = null;

        // Ciclo principale di A*
        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            if (current == null) {
                break;
            }
            if (closedSet.contains(current)) {
                continue;
            }
            closedSet.add(current);

            // Se la posizione corrente è il target (checkpoint), abbiamo finito
            if (current.position.equals(target)) {
                goalNode = current;
                break;
            }

            // Espandiamo i vicini
            for (Vector acc : ACCELERATIONS) {
                Vector newVel = current.velocity.add(acc);
                // Rispettiamo la velocità massima
                if (Math.abs(newVel.getDx()) > MAX_SPEED || Math.abs(newVel.getDy()) > MAX_SPEED) {
                    continue;
                }

                Position newPos = current.position.move(newVel);

                // Verifichiamo i muri
                if (!movementManager.validateMoveTemp(current.position, newVel, gameState.getTrack())) {
                    continue;
                }

                // Creiamo il nodo adiacente
                AStarNode neighbor = new AStarNode(newPos, newVel, current, acc);
                // Costo g = costo attuale + 1
                double tentativeG = current.g + 1.0;

                // Se non lo abbiamo visitato oppure troviamo un costo migliore,
                // aggiorniamo i campi e lo mettiamo nel PQ
                if (!closedSet.contains(neighbor) && tentativeG < neighbor.g) {
                    neighbor.g = tentativeG;
                    neighbor.h = chebyshevDist(newPos, target);
                    neighbor.f = neighbor.g + neighbor.h;
                    openSet.add(neighbor);
                }
            }
        }

        // Se goalNode è rimasto null, nessun percorso trovato
        if (goalNode == null) {
            return new Vector(0,0);
        }

        // Ricostruzione della prima accelerazione
        AStarNode cur = goalNode;
        // Risaliamo finché il parent non è lo startNode o null
        while (cur.parent != null && cur.parent != startNode) {
            cur = cur.parent;
        }

        // L'accelerazione da usare
        Vector chosenAcc = (cur.accApplied != null) ? cur.accApplied : new Vector(0, 0);

        // Ultima verifica: la mossa è valida?
        if (movementManager.validateMove(player, chosenAcc, gameState)) {
            Vector finalVelocity = currentVel.add(chosenAcc);
            Position finalPos = currentPos.move(finalVelocity);

            // Aggiorniamo checkpoint (se attraversato)
            checkpointManager.checkCrossedCheckpoints(player, currentPos, finalPos, gameState.getTrack());

            return chosenAcc;
        }

        // Se la validazione fallisce, fallback a zero
        return new Vector(0,0);
    }

    /**
     * Distanza Chebyshev (max(|dx|,|dy|)).
     * Con costo di 1 per ogni "mossa" (diagonale o no), è ammissibile.
     */
    private double chebyshevDist(Position a, Position b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        return Math.max(dx, dy);
    }

    /**
     * Nodo di A* con (posizione, velocità, parent) + costi g, h, f.
     */
    private static class AStarNode {
        final Position position;
        final Vector velocity;
        final AStarNode parent;
        final Vector accApplied; // accelerazione fatta per passare dal parent a questo

        // Per A*:
        double g = Double.POSITIVE_INFINITY;  // costo accumulato
        double h = 0.0;                       // stima euristica
        double f = 0.0;                       // = g + h

        AStarNode(Position p, Vector v, AStarNode par, Vector acc) {
            this.position = p;
            this.velocity = v;
            this.parent = par;
            this.accApplied = acc;
        }

        // Due AStarNode "uguali" se (position, velocity) sono uguali
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AStarNode)) return false;
            AStarNode other = (AStarNode) o;
            return this.position.equals(other.position) && this.velocity.equals(other.velocity);
        }

        @Override
        public int hashCode() {
            return position.hashCode() * 31 + velocity.hashCode();
        }
    }
}
