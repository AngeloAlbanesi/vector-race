package it.unicam.cs.mdp.vectorrace.view;

/**
 * The {@code CLISpecific} interface defines the specific methods for managing
 * the command-line interface (CLI) of the Vector Race game.
 * Classes that implement this interface provide functionalities to
 * display menus, handle user input, and control the game state
 * in the CLI environment.
 */
public interface CLISpecific {
    /**
     * Shows the circuit selection menu to the user.
     * This menu allows the user to choose from the different circuits available
     * for the game.
     */
    void showCircuitSelection();

    /**
     * Shows the main game menu.
     * This menu offers options such as starting a new game, viewing statistics,
     * or exiting the game.
     */
    void showGameMenu();

    /**
     * Sets the execution state of the game.
     *
     * @param running {@code true} if the game is running, {@code false} otherwise.
     */
    void setGameRunning(boolean running);

    /**
     * Returns the execution state of the game.
     *
     * @return {@code true} if the game is currently running, {@code false} otherwise.
     */
    boolean isGameRunning();
}