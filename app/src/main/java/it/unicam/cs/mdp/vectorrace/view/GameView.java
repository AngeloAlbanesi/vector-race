package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * Interfaccia che definisce i metodi comuni per tutte le viste del gioco.
 * Implementa il pattern Strategy per permettere diverse implementazioni della visualizzazione.
 */
public interface GameView {
    /**
     * Visualizza un messaggio all'utente.
     * 
     * @param message Il messaggio da visualizzare
     */
    void displayMessage(String message);

    /**
     * Visualizza lo stato corrente del gioco.
     * 
     * @param gameState Lo stato del gioco da visualizzare
     */
    void displayGameState(GameState gameState);
}