package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * Interfaccia per il rendering dello stato del gioco.
 * Seguendo il Single Responsibility Principle, questa interfaccia si occupa solo
 * della rappresentazione visiva dello stato del gioco.
 */
public interface IGameRenderer {
    /**
     * Renderizza lo stato del gioco nella forma appropriata.
     * @param gameState lo stato del gioco da renderizzare
     * @return La rappresentazione testuale del gioco
     */
    String renderGameState(GameState gameState);
}