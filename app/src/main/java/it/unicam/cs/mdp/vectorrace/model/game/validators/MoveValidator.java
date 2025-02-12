package it.unicam.cs.mdp.vectorrace.model.game.validators;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Interfaccia per la validazione dei movimenti nel gioco.
 */
public interface MoveValidator {
    /**
     * Valida un movimento da una posizione iniziale a una finale.
     *
     * @param start     posizione di partenza
     * @param end       posizione di arrivo
     * @param player    giocatore che effettua il movimento
     * @param gameState stato corrente del gioco
     * @return true se il movimento è valido, false altrimenti
     */
    boolean isValidMove(Position start, Position end, Player player, GameState gameState);
}