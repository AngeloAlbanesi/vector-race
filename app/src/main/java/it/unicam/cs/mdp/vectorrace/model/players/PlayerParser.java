package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for parsing and validating player configuration data from files.
 * This class handles the reading and interpretation of player specifications,
 * ensuring that all required data is present and valid.
 *
 * <p>File format specification:
 * <ul>
 *   <li>Each line represents one player</li>
 *   <li>Fields are separated by semicolons</li>
 *   <li>Comments start with '#' and are ignored</li>
 *   <li>Required fields: type;name;colorHex</li>
 *   <li>Optional fields: strategy (for bot players)</li>
 * </ul>
 *
 * <p>Example valid lines:
 * <pre>
 * human;Player1;#FF0000
 * bot;AIPlayer;#0000FF;1
 * </pre>
 */
public class PlayerParser {
    private static final String COMMENT_PREFIX = "#";
    private static final String FIELD_SEPARATOR = ";";

    /**
     * Reads and validates player data from a configuration file.
     * Processes the file line by line, skipping comments and empty lines,
     * and validates the format of each player specification.
     *
     * @param filePath Path to the player configuration file.
     * @return List of parsed player data arrays.
     * @throws IOException If file reading fails.
     * @throws PlayerParsingException If the file contains no valid players.
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
     * Parses a single line from the configuration file.
     * Handles empty lines, comments, and validates field count.
     *
     * @param line The line to parse.
     * @return Array of player data fields, or null for skipped lines.
     * @throws PlayerParsingException.InvalidFormatException If line format is invalid.
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
     * Validates and converts raw player data into a structured format.
     * Performs type conversions and validations for all player attributes.
     *
     * @param parts Array of raw player data fields.
     * @return Validated player data object.
     * @throws PlayerParsingException.InvalidColorException If color format is invalid.
     * @throws PlayerParsingException.InvalidStrategyException If bot strategy is invalid.
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
}