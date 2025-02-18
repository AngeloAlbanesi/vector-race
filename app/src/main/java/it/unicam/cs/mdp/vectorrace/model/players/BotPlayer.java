package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * Represents an AI-controlled player in the Vector Race game.
 * This class extends {@link Player} and implements automated movement behavior
 * using configurable AI strategies.
 *
 * <p>Key features:
 * <ul>
 *   <li>AI-driven movement decisions</li>
 *   <li>Configurable racing strategies</li>
 *   <li>Detailed debug logging</li>
 *   <li>Strategy-based acceleration calculation</li>
 * </ul>
 *
 * <p>The bot player uses an {@link AIStrategy} implementation to determine its
 * movements, allowing for different racing behaviors through strategy injection.
 */
public class BotPlayer extends Player {
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final AIStrategy strategy;

    /**
     * Creates a new bot player with specified attributes and racing strategy.
     *
     * @param name The bot's identifier name.
     * @param color The bot's display color.
     * @param startPosition The bot's initial position on the track.
     * @param strategy The AI strategy that will control this bot's movements.
     */
    public BotPlayer(String name, Color color, Position startPosition, AIStrategy strategy) {
        super(name, color, startPosition);
        this.strategy = strategy;
    }

    /**
     * Calculates the next acceleration vector using the bot's AI strategy.
     * This method:
     * <ol>
     *   <li>Logs the current state for debugging</li>
     *   <li>Delegates acceleration calculation to the AI strategy</li>
     *   <li>Logs the calculated acceleration</li>
     * </ol>
     *
     * @param gameState The current state of the game.
     * @return The calculated acceleration vector for the next move.
     */
    @Override
    public Vector getNextAcceleration(GameState gameState) {
        this.logDebug("Calcolo prossima accelerazione");
        this.logDebug("Posizione attuale: " + this.getPosition());
        this.logDebug("Velocità attuale: " + this.getVelocity());
        this.logDebug("Prossimo checkpoint: " + this.getNextCheckpointIndex());

        Vector acceleration = this.strategy.getNextAcceleration(this, gameState);
        this.logDebug("Accelerazione calcolata: " + acceleration);

        return acceleration;
    }

    /**
     * Logs debug information with timestamp and bot identification.
     * The format is: [BOT name - HH:mm:ss.SSS] message
     *
     * @param message The debug message to log.
     */
    private void logDebug(String message) {
        String timestamp = LocalDateTime.now().format(LOG_FORMATTER);
        System.out.printf("[BOT %s - %s] %s%n", this.getName(), timestamp, message);
    }

    /**
     * Gets the AI strategy used by this bot.
     * This can be useful for analysis or strategy adjustment.
     *
     * @return The AI strategy instance controlling this bot.
     */
    public AIStrategy getStrategy() {
        return strategy;
    }
}