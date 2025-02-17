package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;

/**
 * Implementazione dell'interfaccia ICheckpointMap che gestisce la mappa dei
 * checkpoint
 * nel tracciato. Mantiene una mappa di checkpoint organizzati per livello.
 */
public class CheckpointMapManager implements ICheckpointMap {

    private final Map<Integer, Set<Position>> checkpointsByLevel;

    public CheckpointMapManager() {
        this.checkpointsByLevel = new HashMap<>();
    }

    @Override
    public void initialize(Track track) {
        checkpointsByLevel.clear();
        scanTrackForCheckpoints(track);
    }

    @Override
    public Set<Position> getCheckpoints(int level) {
        return checkpointsByLevel.getOrDefault(level, Collections.emptySet());
    }

    /**
     * Scansiona il tracciato per individuare tutti i checkpoint e organizzarli per
     * livello.
     */
    private void scanTrackForCheckpoints(Track track) {
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.getCell(x, y) == CellType.CHECKPOINT) {
                    addCheckpoint(track, x, y);
                }
            }
        }
    }

    /**
     * Aggiunge un checkpoint alla mappa, organizzandolo per livello.
     */
    private void addCheckpoint(Track track, int x, int y) {
        Position position = new Position(x, y);
        int checkpointNumber = track.getCheckpointNumber(position);
        checkpointsByLevel.computeIfAbsent(checkpointNumber, k -> new HashSet<>())
                .add(position);
    }
}