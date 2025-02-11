package it.unicam.cs.mdp.vectorrace.model.core;

import java.io.*;
import java.util.*;

import it.unicam.cs.mdp.vectorrace.model.ai.PriorityData;

/**
 * Classe utility per il caricamento del circuito da un file di testo. Il file
 * utilizza i seguenti simboli:
 * <ul>
 * <li>'#' : Muro (cella non percorribile)</li>
 * <li>'.' : Strada (cella percorribile)</li>
 * <li>'S' : Posizione di partenza</li>
 * <li>'*' : Posizione di arrivo</li>
 * <li>Un carattere numerico (es. '1','2',...) : Checkpoint, l'ordine è dato dal
 * numero</li>
 * </ul>
 */
public class TrackLoader {

    public static Track loadTrack(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Il file del circuito è vuoto: " + filePath);
        }

        int height = lines.size();
        int width = lines.get(0).length();
        CellType[][] grid = new CellType[height][width];
        // Mappa per memorizzare i checkpoint con i loro dati di priorità
        Map<Position, PriorityData> checkpointData = new HashMap<>();

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            if (line.length() != width) {
                throw new IllegalArgumentException(
                        "Tutte le righe devono avere la stessa larghezza. Errore alla riga " + (y + 1));
            }
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if (Character.isDigit(c)) {
                    // Se il carattere è una cifra, consideralo come checkpoint
                    grid[y][x] = CellType.CHECKPOINT;
                    int checkpointNumber = c - '0';
                    // Assegna priorità in base al numero del checkpoint
                    int priorityLevel = calculatePriorityLevel(checkpointNumber);
                    checkpointData.put(
                            new Position(x, y),
                            new PriorityData(priorityLevel, checkpointNumber));
                } else {
                    grid[y][x] = CellType.fromChar(c);
                }
            }
        }
        return new Track(grid, checkpointData);
    }

    /**
     * Calcola il livello di priorità in base al numero del checkpoint:
     * 1-3: priorità bassa (1)
     * 4-6: priorità media (2)
     * 7+: priorità alta (3)
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

    public static List<Position> getStartPositions(Track track) {
        List<Position> startPositions = new ArrayList<>();
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.isStart(x, y)) {
                    startPositions.add(new Position(x, y));
                }
            }
        }
        return startPositions;
    }
}