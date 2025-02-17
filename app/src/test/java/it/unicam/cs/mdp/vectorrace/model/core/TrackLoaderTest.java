package it.unicam.cs.mdp.vectorrace.model.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.PriorityData;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test unitari per la classe TrackLoader.
 */
public class TrackLoaderTest {

    private final String TEST_CIRCUIT_PATH = "src/test/resources/test_circuit.txt";

    @Test
    void testLoadValidTrack() throws IOException {
        Track track = TrackLoader.loadTrack(TEST_CIRCUIT_PATH);
        
        // Verifica dimensioni
        assertEquals(5, track.getWidth(), "La larghezza del circuito deve essere 5");
        assertEquals(5, track.getHeight(), "L'altezza del circuito deve essere 5");
        
        // Verifica celle specifiche
        assertEquals(CellType.START, track.getCell(1, 1), "Deve trovare START in (1,1)");
        assertEquals(CellType.FINISH, track.getCell(3, 2), "Deve trovare FINISH in (3,2)");
        assertEquals(CellType.CHECKPOINT, track.getCell(2, 1), "Deve trovare primo checkpoint in (2,1)");
        assertEquals(CellType.CHECKPOINT, track.getCell(1, 3), "Deve trovare secondo checkpoint in (1,3)");
        assertEquals(CellType.WALL, track.getCell(0, 0), "Deve trovare WALL in (0,0)");
        assertEquals(CellType.ROAD, track.getCell(3, 3), "Deve trovare ROAD in (3,3)");
        
        // Verifica checkpoint
        assertEquals(1, track.getCheckpointNumber(new Position(2, 1)), 
            "Il primo checkpoint deve avere numero 1");
        assertEquals(2, track.getCheckpointNumber(new Position(1, 3)), 
            "Il secondo checkpoint deve avere numero 2");
    }

    @Test
    void testLoadEmptyFile() {
        assertThrows(IOException.class, () -> {
            // Crea un file temporaneo vuoto
            Path tempFile = Files.createTempFile("empty_track", ".txt");
            try {
                TrackLoader.loadTrack(tempFile.toString());
            } finally {
                Files.delete(tempFile);
            }
        }, "Il caricamento di un file vuoto deve lanciare IOException");
    }

    @Test
    void testLoadNonexistentFile() {
        assertThrows(IOException.class, () -> {
            TrackLoader.loadTrack("nonexistent_file.txt");
        }, "Il caricamento di un file non esistente deve lanciare IOException");
    }

    @Test
    void testCheckpointPriorities() throws IOException {
        Track track = TrackLoader.loadTrack(TEST_CIRCUIT_PATH);
        
        // Test priorità dei checkpoint
        Position checkpoint1Pos = new Position(2, 1);
        Position checkpoint2Pos = new Position(1, 3);
        
        assertEquals(1, track.getCheckpointNumber(checkpoint1Pos), 
            "Checkpoint 1 deve avere numero corretto");
        assertEquals(2, track.getCheckpointNumber(checkpoint2Pos), 
            "Checkpoint 2 deve avere numero corretto");
        
        // Verifica che il numero massimo di checkpoint sia corretto
        assertEquals(2, track.getMaxCheckpoint(), 
            "Il numero massimo di checkpoint deve essere 2");
    }

    @Test
    void testInvalidTrackFormat() throws IOException {
        // Crea un file con formato non valido (righe di lunghezza diversa)
        Path tempFile = Files.createTempFile("invalid_track", ".txt");
        try (PrintWriter writer = new PrintWriter(tempFile.toFile())) {
            writer.println("###");
            writer.println("#####");
            writer.println("####");
        }

        assertThrows(IOException.class, () -> {
            TrackLoader.loadTrack(tempFile.toString());
        }, "Il caricamento di un circuito con formato non valido deve lanciare IOException");

        Files.delete(tempFile);
    }

    @Test
    void testBoundaryConditions() throws IOException {
        Track track = TrackLoader.loadTrack(TEST_CIRCUIT_PATH);
        
        // Test celle ai bordi
        for (int x = 0; x < track.getWidth(); x++) {
            assertEquals(CellType.WALL, track.getCell(x, 0), 
                "Il bordo superiore deve essere WALL");
            assertEquals(CellType.WALL, track.getCell(x, track.getHeight() - 1), 
                "Il bordo inferiore deve essere WALL");
        }
        
        for (int y = 0; y < track.getHeight(); y++) {
            assertEquals(CellType.WALL, track.getCell(0, y), 
                "Il bordo sinistro deve essere WALL");
            assertEquals(CellType.WALL, track.getCell(track.getWidth() - 1, y), 
                "Il bordo destro deve essere WALL");
        }
    }
}