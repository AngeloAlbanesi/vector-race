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
        if (lines.isEmpty()) {
            throw new IOException("Il file del circuito è vuoto");
        }

        int height = lines.size();
        int width = lines.get(0).length();
        CellType[][] grid = new CellType[height][width];
        Map<Position, PriorityData> checkpointData = new HashMap<>();

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if (Character.isDigit(c)) {
                    grid[y][x] = CellType.CHECKPOINT;
                    int checkpointNumber = Character.getNumericValue(c);
                    checkpointData.put(
                            new Position(x, y),
                            new PriorityData(calculatePriorityLevel(checkpointNumber), checkpointNumber));
                } else {
                    grid[y][x] = CellType.fromChar(c);
                }
            }
        }

        return new Track(grid, checkpointData);
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