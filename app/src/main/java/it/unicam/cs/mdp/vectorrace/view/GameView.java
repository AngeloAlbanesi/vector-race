package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * The {@code GameView} interface defines the common methods for all views of the Vector Race game.
 * It implements the Strategy pattern to allow different implementations of the visualization,
 * such as a command-line interface (CLI) view or a graphical user interface (GUI) view.
 */
public interface GameView {
    /**
     * Displays a message to the user.
     *
     * @param message The message to display.
     */
    void displayMessage(String message);

    /**
     * Displays the current state of the game.
     *
     * @param gameState The game state to display.
     */
    void displayGameState(GameState gameState);
}