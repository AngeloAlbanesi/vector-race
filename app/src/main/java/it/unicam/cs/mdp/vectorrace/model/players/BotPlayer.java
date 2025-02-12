package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * Rappresenta un giocatore bot che utilizza una strategia AI.
 */
public class BotPlayer extends Player {
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final AIStrategy strategy;

    public BotPlayer(String name, Color color, Position startPosition, AIStrategy strategy) {
        super(name, color, startPosition);
        this.strategy = strategy;
    }

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

    private void logDebug(String message) {
        String timestamp = LocalDateTime.now().format(LOG_FORMATTER);
        System.out.printf("[BOT %s - %s] %s%n", this.getName(), timestamp, message);
    }

    public AIStrategy getStrategy() {
        return strategy;
    }
}