package it.unicam.cs.mdp.vectorrace.model;

import java.util.*;

/**
 * Implementa A* con penalità ridotta (5) se ci si allontana dal target.
 * Disabilita bounding box. Aumenta MAX_EXPANSIONS a 100k per circuiti grandi.
 */
public class PathFinder {

    private static final Vector[] POSSIBLE_ACCELERATIONS = {
        new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1),
        new Vector(0, -1),  new Vector(0, 0),  new Vector(0, 1),
        new Vector(1, -1),  new Vector(1, 0),  new Vector(1, 1)
    };

    // Limiti
    private static final int MAX_SPEED = 5;          // velocità massima
    private static final int MAX_EXPANSIONS = 200000; // esploriamo di più

    // bounding box disabilitato (commentato):
    // private static final int BOUND_DISTANCE = 200;

    private final MovementManager movementManager;

    public PathFinder() {
        this.movementManager = new MovementManager();
    }

    /**
     * Trova un percorso A* dal player al target (checkpoint).
     * Restituisce lo stato finale se ha successo, o null se fallisce.
     */
    public VectorRacePathState findPath(Player player, Position target, GameState gameState) {
        PriorityQueue<VectorRacePathState> openSet = new PriorityQueue<>();
        Set<VectorRacePathState> closedSet = new HashSet<>();

        // Nodo iniziale
        VectorRacePathState start = new VectorRacePathState(player.getPosition(), player.getVelocity(), null);
        start.setG(0);
        start.setH( computeHeuristic(null, player.getPosition(), player.getVelocity(), target) );
        openSet.add(start);

        int expansionsCount = 0;

        while (!openSet.isEmpty()) {
            if (expansionsCount++ > MAX_EXPANSIONS) {
                return null; // troppi nodi esplorati
            }

            VectorRacePathState current = openSet.poll();
            if (current == null) {
                break;
            }

            // Se abbiamo raggiunto il target
            if (current.getPosition().equals(target)) {
                return current;
            }

            if (closedSet.contains(current)) {
                continue;
            }
            closedSet.add(current);

            // No bounding box check, se lo vuoi reintrodurre, usalo qui:
            // if (!withinBoundingBox(current.getPosition(), target)) { ... }

            // Espandiamo i vicini
            for (Vector acc : POSSIBLE_ACCELERATIONS) {
                Vector newVelocity = current.getVelocity().add(acc);

                // Controllo velocità massima
                if (Math.abs(newVelocity.getDx()) > MAX_SPEED || Math.abs(newVelocity.getDy()) > MAX_SPEED) {
                    continue;
                }

                Position newPos = current.getPosition().move(newVelocity);

                // Verifichiamo muri
                if (!movementManager.validateMoveTemp(current.getPosition(), newVelocity, gameState.getTrack())) {
                    continue;
                }

                VectorRacePathState neighbor = new VectorRacePathState(newPos, newVelocity, current);
                double tentativeG = current.getG() + 1;

                // Penalità ridotta
                double hValue = computeHeuristic(current.getPosition(), newPos, newVelocity, target);

                neighbor.setG(tentativeG);
                neighbor.setH(hValue);

                if (!closedSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }
            }
        }

        // Nessun percorso trovato
        return null;
    }

    private double computeHeuristic(Position oldPos, Position newPos, Vector newVel, Position target) {
        double oldDist = (oldPos == null) ? Double.MAX_VALUE : calculateDistance(oldPos, target);
        double newDist = calculateDistance(newPos, target);

        // Penalità ridotta
        double progressPenalty = (newDist > oldDist) ? 5 : 0;

        // Heuristica base: distanza + 0.5*(somma velocita')
        double base = newDist + 0.5 * (Math.abs(newVel.getDx()) + Math.abs(newVel.getDy()));

        return base + progressPenalty;
    }

    private double calculateDistance(Position a, Position b) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Se vuoi reintrodurre bounding box:
    // private boolean withinBoundingBox(Position current, Position target) {
    //     return Math.abs(current.getX() - target.getX()) <= BOUND_DISTANCE
    //         && Math.abs(current.getY() - target.getY()) <= BOUND_DISTANCE;
    // }
}
