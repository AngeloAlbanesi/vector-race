package it.unicam.cs.mdp.vectorrace.model.players;

/**
 * Eccezione personalizzata per errori durante il parsing dei dati dei giocatori.
 */
public class PlayerParsingException extends RuntimeException {
    
    public PlayerParsingException(String message) {
        super(message);
    }

    public PlayerParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class InvalidFormatException extends PlayerParsingException {
        public InvalidFormatException(String line) {
            super("Formato linea non valido: " + line);
        }
    }

    public static class InvalidPlayerTypeException extends PlayerParsingException {
        public InvalidPlayerTypeException(String type) {
            super("Tipo giocatore non valido: " + type);
        }
    }

    public static class InvalidColorException extends PlayerParsingException {
        public InvalidColorException(String color, Throwable cause) {
            super("Colore non valido: " + color, cause);
        }
    }

    public static class InvalidStrategyException extends PlayerParsingException {
        public InvalidStrategyException(String playerName) {
            super("Strategia non specificata per il bot " + playerName);
        }
    }
}