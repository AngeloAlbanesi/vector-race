package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Interfaccia per la validazione delle mosse nel gioco.
 * Separa la logica di validazione del movimento dal resto del codice.
 */
public interface IMoveValidator {
    /**
     * Valida una mossa temporanea durante il pathfinding.
     * 
     * @param start posizione di partenza
     * @param velocity vettore velocità
     * @param track pista di gioco
     * @return true se la mossa è valida
     */
    boolean validateTempMove(Position start, Vector velocity, Track track);

    /**
     * Valida una mossa reale di un giocatore.
     * 
     * @param player giocatore che effettua la mossa
     * @param acceleration vettore accelerazione
     * @param gameState stato del gioco
     * @return true se la mossa è valida
     */
    boolean validateRealMove(Player player, Vector acceleration, GameState gameState);
}