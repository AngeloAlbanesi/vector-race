package it.unicam.cs.mdp.vectorrace.controller;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * An interface defining the contract for the main game controller.
 * Implements the Mediator pattern for communication between components.
 * This interface supports Dependency Inversion Principle (DIP) and facilitates testing
 * by allowing different implementations of the game controller.
 */
public interface IGameController {
    
    /**
     * Gets the current state of the game.
     * This method provides access to the game's state including track, players,
     * and their positions.
     *
     * @return The current game state containing all game-related information.
     */
    GameState getGameState();

    /**
     * Advances the game to the next turn.
     * This method handles the progression of the game by moving to the next player's turn
     * and updating the game state accordingly.
     */
    void advanceTurn();
}