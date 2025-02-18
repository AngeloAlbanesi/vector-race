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
 * Manages the checkpoint map for the Vector Race game.
 * This class implements the {@link ICheckpointMap} interface and is responsible
 * for organizing checkpoints on the track by their level or sequence number.
 *
 * <p>Key features:
 * <ul>
 *   <li>Organizes checkpoints by level for efficient access</li>
 *   <li>Scans the track to identify and store checkpoint positions</li>
 *   <li>Provides access to checkpoints at a specific level</li>
 * </ul>
 */
public class CheckpointMapManager implements ICheckpointMap {

    private final Map<Integer, Set<Position>> checkpointsByLevel;

    /**
     * Creates a new CheckpointMapManager.
     * Initializes the map to store checkpoints organized by level.
     */
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
     * Scans the track to identify all checkpoint positions and organizes them by level.
     * This method iterates through each cell on the track and adds checkpoint
     * positions to the map, using the checkpoint number as the level.
     *
     * @param track The track to scan for checkpoints.
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
     * Adds a checkpoint to the map, organizing it by level.
     *
     * @param track The track containing the checkpoint.
     * @param x The x-coordinate of the checkpoint.
     * @param y The y-coordinate of the checkpoint.
     */
    private void addCheckpoint(Track track, int x, int y) {
        Position position = new Position(x, y);
        int checkpointNumber = track.getCheckpointNumber(position);
        checkpointsByLevel.computeIfAbsent(checkpointNumber, k -> new HashSet<>())
                .add(position);
    }
}