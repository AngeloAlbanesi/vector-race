package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.view.output.ConsoleOutputHandler;
import it.unicam.cs.mdp.vectorrace.view.renderer.CLIGameRenderer;
import it.unicam.cs.mdp.vectorrace.view.renderer.CLIPlayerInfoFormatter;

import java.util.Scanner;

/**
 * The {@code CLIView} class implements the {@code GameView} interface to provide
 * a command-line interface (CLI) visualization of the Vector Race game.
 * It manages the output of the game state and user input through the console.
 */
public class CLIView implements GameView {
    private final Scanner scanner;
    private final CLIGameRenderer gameRenderer;
    private final ConsoleOutputHandler outputHandler;
    private final CLIPlayerInfoFormatter playerInfoFormatter;
    private boolean isGameRunning;

    /**
     * Constructor for the {@code CLIView} class.
     * Initializes the objects needed for visualization and user interaction.
     */
    public CLIView() {
        this.scanner = new Scanner(System.in);
        this.gameRenderer = new CLIGameRenderer();
        this.outputHandler = new ConsoleOutputHandler();
        this.playerInfoFormatter = new CLIPlayerInfoFormatter();
        this.isGameRunning = false;
    }

    /**
     * Displays a message to the user on the console.
     *
     * @param message The message to display.
     */
    @Override
    public void displayMessage(String message) {
        outputHandler.println(message);
    }

    /**
     * Displays the current state of the game on the console.
     *
     * @param gameState The game state to display.
     */
    @Override
    public void displayGameState(GameState gameState) {
        outputHandler.clear();
        outputHandler.println(gameRenderer.renderGame(gameState));
        outputHandler.println(playerInfoFormatter.formatPlayerInfo(gameState.getCurrentPlayer()));
    }

    /**
     * Shows the circuit selection menu to the user.
     * Allows the user to choose from the different circuits available for the game.
     */
    public void showCircuitSelection() {
        outputHandler.println("\nSeleziona il circuito:");
        outputHandler.println("1. Circuito 1");
        outputHandler.println("2. Circuito 2");
        outputHandler.println("3. Circuito 3");
    }

    /**
     * Shows the main game menu to the user.
     * Offers options such as starting an automatic simulation, advancing a turn, or exiting the game.
     */
    public void showGameMenu() {
        outputHandler.println("\nMenu di gioco:");
        outputHandler.println("1. Avvia simulazione automatica");
        outputHandler.println("2. Avanza di un turno");
        outputHandler.println("3. Esci");
    }

    /**
     * Reads a line of input from the console.
     *
     * @return The string read from the console, or {@code null} if an error occurs.
     */
    public String readLine() {
        try {
            return scanner.nextLine();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Displays an error message to the user on the console.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        outputHandler.printError(message);
    }

    /**
     * Returns the execution state of the game.
     *
     * @return {@code true} if the game is running, {@code false} otherwise.
     */
    public boolean isGameRunning() {
        return isGameRunning;
    }

    /**
     * Sets the execution state of the game.
     *
     * @param gameRunning {@code true} if the game is running, {@code false} otherwise.
     */
    public void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }
}