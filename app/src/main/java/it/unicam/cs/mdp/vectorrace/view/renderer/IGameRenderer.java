package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * Interface for the game renderer.
 * Defines the methods needed to display the state of the game.
 */
public interface IGameRenderer {

    /**
     * Renders the current state of the game.
     *
     * @param gameState The state of the game to render.
     * @return A string that represents the state of the game.
     */
    String renderGame(GameState gameState);
}