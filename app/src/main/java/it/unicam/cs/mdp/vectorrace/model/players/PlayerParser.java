package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Classe responsabile del parsing dei dati dei giocatori da file.
 */
public class PlayerParser {
    private static final String COMMENT_PREFIX = "#";
    private static final String FIELD_SEPARATOR = ";";
    
    /**
     * Legge e valida le linee dal file dei giocatori.
     */
    public List<String[]> parsePlayerFile(String filePath) throws IOException {
        List<String[]> playerDataList = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] playerData = parseLine(line);
                if (playerData != null) {
                    playerDataList.add(playerData);
                }
            }
        }
        
        if (playerDataList.isEmpty()) {
            throw new PlayerParsingException("Nessun giocatore valido trovato nel file " + filePath);
        }
        
        return playerDataList;
    }
    
    /**
     * Analizza una singola linea del file.
     */
    private String[] parseLine(String line) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith(COMMENT_PREFIX)) {
            return null;
        }
        
        String[] parts = line.split(FIELD_SEPARATOR);
        if (parts.length < 3) {
            throw new PlayerParsingException.InvalidFormatException(line);
        }
        
        return parts;
    }
    
    /**
     * Valida e converte i dati del giocatore.
     */
    public PlayerData validatePlayerData(String[] parts) {
        String type = parts[0].trim().toLowerCase();
        String name = parts[1].trim();
        String colorHex = parts[2].trim();
        
        Color color;
        try {
            color = Color.decode(colorHex);
        } catch (NumberFormatException e) {
            throw new PlayerParsingException.InvalidColorException(colorHex, e);
        }
        
        StrategyType strategy = null;
        if ("bot".equals(type) && parts.length > 3) {
            try {
                int strategyCode = Integer.parseInt(parts[3].trim());
                strategy = StrategyType.fromCode(strategyCode);
            } catch (NumberFormatException e) {
                throw new PlayerParsingException.InvalidStrategyException(name);
            }
        }
        
        return new PlayerData(type, name, color, strategy);
    }
    
    /**
     * Classe interna per contenere i dati validati del giocatore.
     */
    public static class PlayerData {
        private final String type;
        private final String name;
        private final Color color;
        private final StrategyType strategy;
        
        public PlayerData(String type, String name, Color color, StrategyType strategy) {
            this.type = type;
            this.name = name;
            this.color = color;
            this.strategy = strategy;
        }
        
        public String getType() { return type; }
        public String getName() { return name; }
        public Color getColor() { return color; }
        public StrategyType getStrategy() { return strategy; }
    }
}