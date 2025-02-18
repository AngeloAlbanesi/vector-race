package it.unicam.cs.mdp.vectorrace.model.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.PriorityData;

/**
 * Responsible for loading and parsing track files in the Vector Race game.
 * This class implements the Single Responsibility Principle by focusing solely on track loading logic.
 * 
 * <p>Track files are text-based and follow these conventions:
 * <ul>
 *   <li>'#' - Wall cells that cannot be traversed</li>
 *   <li>'.' - Road cells that can be traversed</li>
 *   <li>'S' - Start positions (minimum 2 required)</li>
 *   <li>'*' - Finish line cells (minimum 1 required)</li>
 *   <li>Digits - Checkpoint cells with their sequence number</li>
 * </ul>
 * 
 * <p>The loader performs several validations:
 * <ul>
 *   <li>Ensures minimum number of start positions (2)</li>
 *   <li>Verifies presence of finish line</li>
 *   <li>Checks for minimum number of checkpoints (1)</li>
 *   <li>Validates file format and content</li>
 * </ul>
 */
public class TrackLoader {
    
    /**
     * Loads a track from a file.
     * This method orchestrates the complete loading process including:
     * <ul>
     *   <li>Reading the file content</li>
     *   <li>Validating track requirements</li>
     *   <li>Creating the grid structure</li>
     *   <li>Processing checkpoint data</li>
     * </ul>
     * 
     * @param path The path to the track file.
     * @return A fully initialized {@link Track} instance.
     * @throws IOException If file reading fails or track validation fails.
     */
    public static Track loadTrack(String path) throws IOException {
        List<String> lines = readLines(path);
        validateTrackFile(lines);
        
        int height = lines.size();
        int width = lines.get(0).length();
        
        CellType[][] grid = createEmptyGrid(height, width);
        Map<Position, PriorityData> checkpointData = new HashMap<>();
        
        populateGridAndCheckpoints(grid, checkpointData, lines);
        
        return new Track(grid, checkpointData);
    }

    /**
     * Validates the track file content against game requirements.
     * Performs essential checks to ensure the track is playable:
     * <ul>
     *   <li>Verifies file is not empty</li>
     *   <li>Counts and validates start positions (minimum 2)</li>
     *   <li>Verifies finish line presence (minimum 1 cell)</li>
     *   <li>Validates checkpoint presence (minimum 1)</li>
     * </ul>
     *
     * @param lines The lines read from the track file.
     * @throws IOException If any validation check fails.
     */
    private static void validateTrackFile(List<String> lines) throws IOException {
        if (lines.isEmpty()) {
            throw new IOException("Il file del circuito è vuoto");
        }
        
        int startCount = 0;
        int finishCount = 0;
        int checkpointCount = 0;
        
        for (String line : lines) {
            for (char c : line.toCharArray()) {
                if (c == 'S') {
                    startCount++;
                } else if (c == '*') {
                    finishCount++;
                } else if (Character.isDigit(c)) {
                    checkpointCount++;
                }
            }
        }
        
        if (startCount < 2) {
            throw new IOException("Numero insufficiente di posizioni di partenza (minimo 2)");
        }
        if (finishCount < 1) {
            throw new IOException("Deve esserci almeno una posizione di arrivo");
        }
        if (checkpointCount < 1) {
            throw new IOException("Numero insufficiente di checkpoint (minimo 1)");
        }
    }

    /**
     * Creates an empty grid with the specified dimensions.
     * Initializes a 2D array that will hold the track layout.
     *
     * @param height The number of rows in the grid.
     * @param width The number of columns in the grid.
     * @return An empty 2D array of {@link CellType}.
     */
    private static CellType[][] createEmptyGrid(int height, int width) {
        return new CellType[height][width];
    }

    /**
     * Populates the grid and checkpoint data from the file content.
     * Processes each character in the file to build the track layout
     * and initialize checkpoint information.
     *
     * @param grid The grid to populate with cell types.
     * @param checkpointData Map to populate with checkpoint information.
     * @param lines The lines read from the track file.
     */
    private static void populateGridAndCheckpoints(CellType[][] grid, Map<Position, PriorityData> checkpointData, List<String> lines) {
        for (int y = 0; y < grid.length; y++) {
            String line = lines.get(y);
            for (int x = 0; x < grid[y].length; x++) {
                processCellCharacter(grid, checkpointData, line.charAt(x), x, y);
            }
        }
    }

    /**
     * Processes a single character from the track file.
     * Converts the character into appropriate cell type and handles
     * checkpoint-specific processing if needed.
     *
     * @param grid The grid being populated.
     * @param checkpointData Map of checkpoint data being populated.
     * @param c The character to process.
     * @param x The x-coordinate in the grid.
     * @param y The y-coordinate in the grid.
     */
    private static void processCellCharacter(CellType[][] grid, Map<Position, PriorityData> checkpointData,
            char c, int x, int y) {
        if (Character.isDigit(c)) {
            processCheckpoint(grid, checkpointData, c, x, y);
        } else {
            grid[y][x] = CellType.fromChar(c);
        }
    }

    /**
     * Processes a checkpoint cell.
     * Sets up both the grid cell type and the associated checkpoint data
     * including priority level calculation.
     *
     * @param grid The grid being populated.
     * @param checkpointData Map of checkpoint data being populated.
     * @param c The checkpoint number character.
     * @param x The x-coordinate in the grid.
     * @param y The y-coordinate in the grid.
     */
    private static void processCheckpoint(CellType[][] grid, Map<Position, PriorityData> checkpointData,
            char c, int x, int y) {
        grid[y][x] = CellType.CHECKPOINT;
        int checkpointNumber = Character.getNumericValue(c);
        checkpointData.put(
                new Position(x, y),
                new PriorityData(calculatePriorityLevel(checkpointNumber), checkpointNumber));
    }

    /**
     * Reads all non-empty lines from the track file.
     * Handles file access and basic content reading.
     *
     * @param path The path to the track file.
     * @return List of non-empty lines from the file.
     * @throws IOException If file reading fails.
     */
    private static List<String> readLines(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Calculates the priority level for a checkpoint based on its number.
     * Priority levels are assigned as follows:
     * <ul>
     *   <li>1-3: Low priority (level 1)</li>
     *   <li>4-6: Medium priority (level 2)</li>
     *   <li>7+: High priority (level 3)</li>
     * </ul>
     *
     * @param checkpointNumber The checkpoint's sequence number.
     * @return The calculated priority level (1-3).
     */
    private static int calculatePriorityLevel(int checkpointNumber) {
        if (checkpointNumber <= 3) {
            return 1; // Low priority
        } else if (checkpointNumber <= 6) {
            return 2; // Medium priority
        } else {
            return 3; // High priority
        }
    }
}