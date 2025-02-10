package it.unicam.cs.mdp.vectorrace.model;

import java.util.*;

public class PathFinder {
    private static final Vector[] POSSIBLE_ACCELERATIONS = {
            new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1),
            new Vector(0, -1), new Vector(0, 0), new Vector(0, 1),
            new Vector(1, -1), new Vector(1, 0), new Vector(1, 1)
    };

    private final MovementManager movementManager;

    public PathFinder() {
        this.movementManager = new MovementManager();
    }

    public VectorRacePathState findPath(Player player, Position target, GameState gameState) {
        PriorityQueue<VectorRacePathState> openSet = new PriorityQueue<>();
        Set<VectorRacePathState> closedSet = new HashSet<>();

        VectorRacePathState start = new VectorRacePathState(player.getPosition(), player.getVelocity(), null);
        start.setG(0);
        start.setH(computeHeuristic(start.getPosition(), start.getVelocity(), target));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            VectorRacePathState current = openSet.poll();
            if (current.getPosition().equals(target)) {
                return current;
            }

            closedSet.add(current);

            for (Vector acceleration : POSSIBLE_ACCELERATIONS) {
                Vector newVelocity = current.getVelocity().add(acceleration);
                Position newPosition = current.getPosition().move(newVelocity);

                VectorRacePathState neighbor = new VectorRacePathState(newPosition, newVelocity, current);

                if (closedSet.contains(neighbor) ||
                        !movementManager.validateMove(player, acceleration, gameState)) {
                    continue;
                }

                double tentativeG = current.getG() + 1;

                if (!openSet.contains(neighbor)) {
                    neighbor.setG(tentativeG);
                    neighbor.setH(computeHeuristic(newPosition, newVelocity, target));
                    openSet.add(neighbor);
                } else if (tentativeG < neighbor.getG()) {
                    neighbor.setG(tentativeG);
                }
            }
        }
        return null;
    }

    private double computeHeuristic(Position pos, Vector velocity, Position target) {
        double distance = calculateDistance(pos, target);
        double velocityPenalty = Math.abs(velocity.getDx()) + Math.abs(velocity.getDy());
        return distance + (velocityPenalty * 0.5);
    }

    private double calculateDistance(Position a, Position b) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}