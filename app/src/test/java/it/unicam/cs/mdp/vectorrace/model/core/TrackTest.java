package it.unicam.cs.mdp.vectorrace.model.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.PriorityData;
import java.util.*;

/**
 * Test unitari per la classe Track.
 */
public class TrackTest {
    private Track track;
    private CellType[][] grid;
    private Map<Position, PriorityData> checkpointData;
    private final int width = 5;
    private final int height = 5;

    @BeforeEach
    void setUp() {
        // Creazione di una griglia 5x5 di test
        grid = new CellType[][]{
            {CellType.WALL,  CellType.WALL,  CellType.WALL,  CellType.WALL,  CellType.WALL},
            {CellType.WALL,  CellType.START, CellType.ROAD,  CellType.CHECKPOINT, CellType.WALL},
            {CellType.WALL,  CellType.ROAD,  CellType.ROAD,  CellType.ROAD, CellType.WALL},
            {CellType.WALL,  CellType.CHECKPOINT, CellType.ROAD, CellType.FINISH, CellType.WALL},
            {CellType.WALL,  CellType.WALL,  CellType.WALL,  CellType.WALL, CellType.WALL}
        };

        // Creazione della mappa dei checkpoint
        checkpointData = new HashMap<>();
        checkpointData.put(new Position(3, 1), new PriorityData(1, 1));
        checkpointData.put(new Position(1, 3), new PriorityData(2, 2));

        track = new Track(grid, checkpointData);
    }

    @Test
    void testDimensioni() {
        assertEquals(width, track.getWidth(), "La larghezza della pista deve essere corretta");
        assertEquals(height, track.getHeight(), "L'altezza della pista deve essere corretta");
    }

    @Test
    void testIsWithinBounds() {
        assertTrue(track.isWithinBounds(0, 0), "L'angolo (0,0) deve essere dentro i bounds");
        assertTrue(track.isWithinBounds(4, 4), "L'angolo (4,4) deve essere dentro i bounds");
        assertFalse(track.isWithinBounds(-1, 0), "Coordinate negative devono essere fuori bounds");
        assertFalse(track.isWithinBounds(0, -1), "Coordinate negative devono essere fuori bounds");
        assertFalse(track.isWithinBounds(5, 0), "Coordinate oltre width devono essere fuori bounds");
        assertFalse(track.isWithinBounds(0, 5), "Coordinate oltre height devono essere fuori bounds");
    }

    @Test
    void testGetCell() {
        assertEquals(CellType.START, track.getCell(1, 1), "Deve trovare la cella START");
        assertEquals(CellType.FINISH, track.getCell(3, 3), "Deve trovare la cella FINISH");
        assertEquals(CellType.CHECKPOINT, track.getCell(3, 1), "Deve trovare il primo CHECKPOINT");
        assertEquals(CellType.CHECKPOINT, track.getCell(1, 3), "Deve trovare il secondo CHECKPOINT");
        assertEquals(CellType.WALL, track.getCell(-1, 0), "Fuori bounds deve restituire WALL");
    }

    @Test
    void testIsPassable() {
        assertTrue(track.isPassable(1, 1), "START deve essere percorribile");
        assertTrue(track.isPassable(3, 3), "FINISH deve essere percorribile");
        assertTrue(track.isPassable(3, 1), "CHECKPOINT deve essere percorribile");
        assertTrue(track.isPassable(2, 2), "ROAD deve essere percorribile");
        assertFalse(track.isPassable(0, 0), "WALL non deve essere percorribile");
        assertFalse(track.isPassable(-1, 0), "Fuori bounds non deve essere percorribile");
    }

    @Test
    void testStartAndFinish() {
        assertTrue(track.isStart(1, 1), "Deve identificare la cella START");
        assertFalse(track.isStart(0, 0), "Non deve identificare START dove non c'è");
        assertTrue(track.isFinish(3, 3), "Deve identificare la cella FINISH");
        assertFalse(track.isFinish(0, 0), "Non deve identificare FINISH dove non c'è");
    }

    @Test
    void testGetFinishPositions() {
        List<Position> finishPositions = track.getFinishPositions();
        assertEquals(1, finishPositions.size(), "Deve trovare esattamente una posizione FINISH");
        assertTrue(finishPositions.contains(new Position(3, 3)), 
            "La posizione FINISH deve essere (3,3)");
    }

    @Test
    void testCheckpointManagement() {
        Position checkpoint1 = new Position(3, 1);
        Position checkpoint2 = new Position(1, 3);
        Position nonCheckpoint = new Position(2, 2);

        // Test getCheckpointNumber
        assertEquals(1, track.getCheckpointNumber(checkpoint1), 
            "Il primo checkpoint deve avere numero 1");
        assertEquals(2, track.getCheckpointNumber(checkpoint2), 
            "Il secondo checkpoint deve avere numero 2");
        assertEquals(-1, track.getCheckpointNumber(nonCheckpoint), 
            "Una posizione non checkpoint deve restituire -1");

        // Test getMaxCheckpoint
        assertEquals(2, track.getMaxCheckpoint(), 
            "Il numero massimo di checkpoint deve essere 2");

        // Test setCheckpointReached e hasReachedCheckpointInRow
        assertFalse(track.hasReachedCheckpointInRow(1, 1), 
            "Il checkpoint non dovrebbe essere raggiunto inizialmente");
        track.setCheckpointReached(checkpoint1);
        assertTrue(track.hasReachedCheckpointInRow(1, 1), 
            "Il checkpoint dovrebbe risultare raggiunto dopo setCheckpointReached");
    }
}