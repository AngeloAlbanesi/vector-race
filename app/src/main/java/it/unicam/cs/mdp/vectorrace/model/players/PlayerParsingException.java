package it.unicam.cs.mdp.vectorrace.model.players;

/**
 * Custom exception hierarchy for handling player data parsing errors.
 * This class and its subclasses provide specific exceptions for different
 * types of errors that can occur during player configuration parsing.
 * 
 * <p>The exception hierarchy includes:
 * <ul>
 *   <li>{@link InvalidFormatException} - For general format errors</li>
 *   <li>{@link InvalidPlayerTypeException} - For invalid player type specifications</li>
 *   <li>{@link InvalidColorException} - For invalid color format or values</li>
 *   <li>{@link InvalidStrategyException} - For missing or invalid bot strategies</li>
 * </ul>
 */
public class PlayerParsingException extends RuntimeException {
    
    /**
     * Creates a new PlayerParsingException with a specific error message.
     *
     * @param message The detailed error message.
     */
    public PlayerParsingException(String message) {
        super(message);
    }

    /**
     * Creates a new PlayerParsingException with a message and underlying cause.
     *
     * @param message The detailed error message.
     * @param cause The underlying cause of the error.
     */
    public PlayerParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception thrown when a player configuration line has invalid format.
     * This typically occurs when the line doesn't match the expected pattern
     * or is missing required fields.
     */
    public static class InvalidFormatException extends PlayerParsingException {
        /**
         * @param line The invalid configuration line that caused the error.
         */
        public InvalidFormatException(String line) {
            super("Formato linea non valido: " + line);
        }
    }

    /**
     * Exception thrown when an invalid player type is specified.
     * Valid player types are typically "human" or "bot".
     */
    public static class InvalidPlayerTypeException extends PlayerParsingException {
        /**
         * @param type The invalid player type that was specified.
         */
        public InvalidPlayerTypeException(String type) {
            super("Tipo giocatore non valido: " + type);
        }
    }

    /**
     * Exception thrown when a player's color specification is invalid.
     * This can occur due to invalid format or unsupported color values.
     */
    public static class InvalidColorException extends PlayerParsingException {
        /**
         * @param color The invalid color specification.
         * @param cause The underlying cause of the color parsing error.
         */
        public InvalidColorException(String color, Throwable cause) {
            super("Colore non valido: " + color, cause);
        }
    }

    /**
     * Exception thrown when a bot player is missing its required strategy specification.
     * Bot players must have a valid strategy ({@link StrategyType}) specified
     * to determine their behavior.
     */
    public static class InvalidStrategyException extends PlayerParsingException {
        /**
         * @param playerName The name of the bot player missing a strategy.
         */
        public InvalidStrategyException(String playerName) {
            super("Strategia non specificata per il bot " + playerName);
        }
    }
}