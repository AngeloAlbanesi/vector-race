package it.unicam.cs.mdp.vectorrace.model;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta un giocatore bot che utilizza una strategia AI.
 */
public class BotPlayer extends Player {

    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final AIStrategy strategy;

    public void logDebug(String message) {
        String timestamp = LocalDateTime.now().format(LOG_FORMATTER);
        System.out.printf("[DEBUG %s] [BOT: %s] %s%n", timestamp, getName(), message);
    }

    /**
     * Costruttore.
     *
     * @param name          nome del bot
     * @param color         colore per visualizzazione
     * @param startPosition posizione iniziale
     * @param strategy      strategia AI da utilizzare
     */
    public BotPlayer(String name, Color color, Position startPosition, AIStrategy strategy) {
        super(name, color, startPosition);
        this.strategy = strategy;
    }

    public AIStrategy getStrategy() {
        return strategy;
    }

    @Override
    public Vector getNextAcceleration(GameState snapshot) {
        logDebug("Calcolo prossima accelerazione");
        logDebug("Posizione attuale: " + getPosition());
        logDebug("Velocità attuale: " + getVelocity());
        logDebug("Prossimo checkpoint: " + getNextCheckpointIndex());

        Vector acceleration = strategy.getNextAcceleration(this, snapshot);

        logDebug("Accelerazione calcolata: " + acceleration);
        return acceleration;
    }

}