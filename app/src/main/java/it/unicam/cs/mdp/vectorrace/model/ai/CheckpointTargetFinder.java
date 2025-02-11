package it.unicam.cs.mdp.vectorrace.model.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Classe che determina la cella "target" (il checkpoint successivo o il
 * traguardo)
 * per un giocatore.
 */
public class CheckpointTargetFinder {

    private final Map<Integer, Set<Position>> checkpointsByLevel = new HashMap<>();
    private final Map<String, Position> playerTargets = new HashMap<>();
    private boolean checkpointsMapInitialized = false;

    /**
     * Restituisce la prossima posizione di checkpoint (o finish) che il giocatore
     * deve raggiungere.
     */
    public Position findNextTarget(Player player, GameState gameState) {
        if (!checkpointsMapInitialized) {
            updateCheckpointMap(gameState.getTrack());
        }

        int currentCheckpointIndex = player.getNextCheckpointIndex();
        Track track = gameState.getTrack();

        // Se abbiamo superato il numero massimo di checkpoint, puntiamo al FINISH
        int maxCheckpoint = track.getMaxCheckpoint();
        if (currentCheckpointIndex > maxCheckpoint) {
            return findFinishCell(track);
        }

        // Se per qualche motivo non troviamo checkpoint con quell'indice, andiamo al
        // FINISH
        if (!checkpointsByLevel.containsKey(currentCheckpointIndex)) {
            return findFinishCell(track);
        }

        // Tutte le celle del checkpoint 'currentCheckpointIndex'
        Set<Position> checkpoints = checkpointsByLevel.get(currentCheckpointIndex);

        // Verifichiamo se il giocatore ha già un target valido in memoria
        Position currentTarget = playerTargets.get(player.getName());
        if (currentTarget != null && checkpoints.contains(currentTarget)) {
            return currentTarget;
        }

        // Altrimenti, scegliamo la cella checkpoint più vicina
        Position bestCheckpoint = null;
        double bestDist = Double.MAX_VALUE;
        for (Position cp : checkpoints) {
            double dist = cp.distanceTo(player.getPosition());
            if (dist < bestDist) {
                bestDist = dist;
                bestCheckpoint = cp;
            }
        }

        // Se non c'è nessun checkpoint valido, fallback al finish
        if (bestCheckpoint == null) {
            return findFinishCell(track);
        }

        // Salviamo come target e restituiamo
        playerTargets.put(player.getName(), bestCheckpoint);
        return bestCheckpoint;
    }

    private void updateCheckpointMap(Track track) {
        checkpointsByLevel.clear();
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.getCell(x, y) == CellType.CHECKPOINT) {
                    int checkpointNumber = track.getCheckpointNumber(new Position(x, y));
                    checkpointsByLevel
                            .computeIfAbsent(checkpointNumber, k -> new HashSet<>())
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
}
