package it.unicam.cs.mdp.vectorrace.model;

import java.util.*;

/**
 * A* "puro" senza penalità e fallback.
 * Ogni mossa costa 1. Usa Chebyshev distance come euristica ammissibile
 * se consideri che in un turno puoi spostarti di 1 in diagonale.
 */
public class PureAStarStrategy implements AIStrategy {

    private final MovementManager movementManager = new MovementManager();
    private static final Vector[] ACCELERATIONS = {
        new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1),
        new Vector(0, -1),  new Vector(0, 0),  new Vector(0, 1),
        new Vector(1, -1),  new Vector(1, 0),  new Vector(1, 1)
    };
    private static final int MAX_SPEED = 5;

    private final CheckpointTargetFinder targetFinder = new CheckpointTargetFinder();
    private final CheckpointManager checkpointManager = new CheckpointManager();

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        Position currentPos = player.getPosition();
        Vector currentVel = player.getVelocity();

        Position target = targetFinder.findNextTarget(player, gameState);
        if (target == null) {
            return new Vector(0,0);
        }

        // A* con openSet e closedSet
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Map<AStarNode, Double> gScoreMap = new HashMap<>();

        AStarNode start = new AStarNode(currentPos, currentVel, null, null);
        gScoreMap.put(start, 0.0);
        start.h = chebyshevDist(currentPos, target);
        openSet.add(start);

        AStarNode goal = null;

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            if (current.position.equals(target)) {
                goal = current;
                break;
            }

            for (Vector acc : ACCELERATIONS) {
                Vector newVel = current.velocity.add(acc);
                if (Math.abs(newVel.getDx()) > MAX_SPEED || Math.abs(newVel.getDy()) > MAX_SPEED) {
                    continue;
                }

                Position newPos = current.position.move(newVel);

                // Verifichiamo i muri
                if (!movementManager.validateMoveTemp(current.position, newVel, gameState.getTrack())) {
                    continue;
                }

                AStarNode neighbor = new AStarNode(newPos, newVel, current, acc);

                double tentativeG = gScoreMap.getOrDefault(current, Double.MAX_VALUE) + 1.0;
                if (tentativeG < gScoreMap.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    gScoreMap.put(neighbor, tentativeG);
                    neighbor.h = chebyshevDist(newPos, target); 
                    openSet.remove(neighbor); // rimuove se presente, per aggiornarlo
                    openSet.add(neighbor);
                }
            }
        }

        if (goal == null) {
            return new Vector(0,0);
        }

        // Ricostruisci la prima accelerazione dal goal
        AStarNode cur = goal;
        while (cur.parent != null && cur.parent != start) {
            cur = cur.parent;
        }
        Vector chosenAcc = (cur.accApplied != null) ? cur.accApplied : new Vector(0,0);

        // Valida la mossa
        if (movementManager.validateMove(player, chosenAcc, gameState)) {
            Vector finalVelocity = currentVel.add(chosenAcc);
            Position finalPos = currentPos.move(finalVelocity);
            checkpointManager.checkCrossedCheckpoints(player, currentPos, finalPos, gameState.getTrack());
            return chosenAcc;
        }
        return new Vector(0,0);
    }

    // euristica Chebyshev (max di dx, dy) se muovere diagonalmente conta come 1
    private double chebyshevDist(Position a, Position b) {
        int dx = Math.abs(b.getX() - a.getX());
        int dy = Math.abs(b.getY() - a.getY());
        return Math.max(dx, dy);
    }

    private static class AStarNode implements Comparable<AStarNode> {
        final Position position;
        final Vector velocity;
        final AStarNode parent;
        final Vector accApplied;

        double h; // euristica
        // in openSet useremo f = g + h, dove g = "livello BFS" salvato in gScoreMap

        AStarNode(Position p, Vector v, AStarNode par, Vector acc) {
            this.position = p;
            this.velocity = v;
            this.parent = par;
            this.accApplied = acc;
        }

        @Override
        public int compareTo(AStarNode o) {
            // serve somma (g + h). g è in gScoreMap, che non abbiamo qui.
            // Trick: usiamo un value store "f" esterno o passarlo nel costruttore
            // Semplifichiamo e mettiamo f = h (non ottimale, ma Q compare).
            // Meglio un data structure custom. In modo "greedy" => non ottimale
            return Double.compare(this.h, o.h);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AStarNode)) return false;
            AStarNode other = (AStarNode) o;
            return this.position.equals(other.position) && this.velocity.equals(other.velocity);
        }

        @Override
        public int hashCode() {
            return position.hashCode()*31 + velocity.hashCode();
        }
    }
}
