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
 * Classe responsabile del caricamento e parsing dei file del circuito.
 * Implementa il Single Responsibility Principle gestendo solo la logica di caricamento tracciato.
 */
public class TrackLoader {
    
    /**
     * Carica un circuito da file.
     * 
     * @param path Il percorso del file del circuito
     * @return Il circuito caricato
     * @throws IOException Se si verificano errori durante la lettura del file
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
     * Valida il file del circuito.
     *
     * @param lines Le linee del file
     * @throws IOException Se il file non è valido
     */
    private static void validateTrackFile(List<String> lines) throws IOException {
        if (lines.isEmpty()) {
            throw new IOException("Il file del circuito è vuoto");
        }
    }

    /**
     * Crea una griglia vuota con le dimensioni specificate.
     *
     * @param height Altezza della griglia
     * @param width Larghezza della griglia
     * @return La griglia creata
     */
    private static CellType[][] createEmptyGrid(int height, int width) {
        return new CellType[height][width];
    }

    /**
     * Popola la griglia e i dati dei checkpoint.
     *
     * @param grid La griglia da popolare
     * @param checkpointData I dati dei checkpoint
     * @param lines Le linee del file
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
     * Processa un carattere della griglia e aggiorna la cella corrispondente.
     *
     * @param grid La griglia
     * @param checkpointData I dati dei checkpoint
     * @param c Il carattere da processare
     * @param x La coordinata x
     * @param y La coordinata y
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
     * Processa un checkpoint e aggiorna la griglia e i dati dei checkpoint.
     *
     * @param grid La griglia
     * @param checkpointData I dati dei checkpoint
     * @param c Il carattere del checkpoint
     * @param x La coordinata x
     * @param y La coordinata y
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
     * Legge le linee del file.
     * 
     * @param path Il percorso del file
     * @return Lista delle linee lette
     * @throws IOException Se si verificano errori durante la lettura
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
     * Calcola il livello di priorità di un checkpoint.
     * 
     * @param checkpointNumber Il numero del checkpoint
     * @return Il livello di priorità calcolato
     */
    private static int calculatePriorityLevel(int checkpointNumber) {
        if (checkpointNumber <= 3) {
            return 1; // Priorità bassa
        } else if (checkpointNumber <= 6) {
            return 2; // Priorità media
        } else {
            return 3; // Priorità alta
        }
    }
}