package it.unicam.cs.mdp.vectorrace.model;

import java.util.*;

public class CheckpointTargetFinder {
    private final Map<Integer, Set<Position>> checkpointsByLevel = new HashMap<>();
    private final Map<String, Position> playerTargets = new HashMap<>();
    private boolean checkpointsMapInitialized = false;

    public Position findNextTarget(Player player, GameState gameState) {
        if (!checkpointsMapInitialized) {
            updateCheckpointMap(gameState.getTrack());
        }

        int currentCheckpointIndex = player.getNextCheckpointIndex();
        Track track = gameState.getTrack();

        // Se non ci sono checkpoint per il livello corrente, prova con il traguardo
        if (!checkpointsByLevel.containsKey(currentCheckpointIndex)) {
            return findFinishCell(track);
        }

        Set<Position> checkpoints = checkpointsByLevel.get(currentCheckpointIndex);
        Position currentTarget = playerTargets.get(player.getName());

        // Se il target corrente è ancora valido, mantienilo
        if (currentTarget != null && checkpoints.contains(currentTarget)) {
            return currentTarget;
        }

        // Cerca il miglior checkpoint disponibile
        Position bestCheckpoint = null;
        double bestScore = Double.MAX_VALUE;

        for (Position cp : checkpoints) {
            double distance = calculateDistance(player.getPosition(), cp);

            // Considera la direzione attuale del movimento
            Vector velocity = player.getVelocity();
            if (!velocity.isZero()) {
                int dx = cp.getX() - player.getPosition().getX();
                int dy = cp.getY() - player.getPosition().getY();
                double angleDiff = Math.abs(Math.atan2(dy, dx) - Math.atan2(velocity.getDy(), velocity.getDx()));
                distance += angleDiff * 20;
            }

            if (distance < bestScore) {
                bestScore = distance;
                bestCheckpoint = cp;
            }
        }

        if (bestCheckpoint != null) {
            playerTargets.put(player.getName(), bestCheckpoint);
            return bestCheckpoint;
        }

        return findFinishCell(track);
    }

    private void updateCheckpointMap(Track track) {
        if (!checkpointsByLevel.isEmpty()) {
            return;
        }
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.getCell(x, y) == CellType.CHECKPOINT) {
                    int level = track.getCheckpointNumber(new Position(x, y));
                    checkpointsByLevel.computeIfAbsent(level, k -> new HashSet<>())
                            .add(new Position(x, y));
                }
            }
        }
        checkpointsMapInitialized = true;
    }

    private Position findFinishCell(Track track) {
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.getCell(x, y) == CellType.FINISH) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    private double calculateDistance(Position a, Position b) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}