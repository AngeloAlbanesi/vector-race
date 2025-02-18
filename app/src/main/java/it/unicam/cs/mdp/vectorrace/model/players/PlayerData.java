package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;

/**
 * Data container class that holds validated player configuration information.
 * This immutable class encapsulates all the necessary data to create a player
 * instance, ensuring data consistency and type safety.
 *
 * <p>The class stores:
 * <ul>
 *   <li>Player type (human/bot)</li>
 *   <li>Player name</li>
 *   <li>Player color</li>
 *   <li>AI strategy (for bot players)</li>
 * </ul>
 */
public class PlayerData {
    private final String type;
    private final String name;
    private final Color color;
    private final StrategyType strategy;

    /**
     * Creates a new PlayerData instance with the specified attributes.
     * All fields are final to ensure immutability.
     *
     * @param type The player type ("human" or "bot").
     * @param name The player's name.
     * @param color The player's display color.
     * @param strategy The AI strategy (null for human players).
     */
    public PlayerData(String type, String name, Color color, StrategyType strategy) {
        this.type = type;
        this.name = name;
        this.color = color;
        this.strategy = strategy;
    }

    /**
     * Gets the player type.
     *
     * @return The player type ("human" or "bot").
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the player name.
     *
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player color.
     *
     * @return The color used for displaying the player.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the AI strategy.
     *
     * @return The strategy type for bot players, null for human players.
     */
    public StrategyType getStrategy() {
        return strategy;
    }

    @Override
    public String toString() {
        return String.format("PlayerData[type=%s, name=%s, strategy=%s]",
            type, name, strategy != null ? strategy.name() : "N/A");
    }
}