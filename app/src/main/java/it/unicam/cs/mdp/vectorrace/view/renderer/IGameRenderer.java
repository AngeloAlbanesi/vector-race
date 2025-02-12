package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * Interfaccia per il rendering del gioco.
 * Definisce i metodi necessari per visualizzare lo stato del gioco.
 */
public interface IGameRenderer {
    
    /**
     * Renderizza lo stato corrente del gioco.
     *
     * @param gameState Lo stato del gioco da renderizzare
     * @return Una stringa che rappresenta lo stato del gioco
     */
    String renderGame(GameState gameState);
}