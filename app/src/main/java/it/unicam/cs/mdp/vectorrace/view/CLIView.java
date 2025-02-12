package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.view.output.ConsoleOutputHandler;
import it.unicam.cs.mdp.vectorrace.view.renderer.CLIGameRenderer;
import it.unicam.cs.mdp.vectorrace.view.renderer.CLIPlayerInfoFormatter;

import java.util.Scanner;

/**
 * Implementazione della vista CLI del gioco.
 * Gestisce la visualizzazione dello stato del gioco e l'interazione con l'utente
 * attraverso la console.
 */
public class CLIView implements GameView {
    private final Scanner scanner;
    private final CLIGameRenderer gameRenderer;
    private final ConsoleOutputHandler outputHandler;
    private final CLIPlayerInfoFormatter playerInfoFormatter;
    private boolean isGameRunning;

    public CLIView() {
        this.scanner = new Scanner(System.in);
        this.gameRenderer = new CLIGameRenderer();
        this.outputHandler = new ConsoleOutputHandler();
        this.playerInfoFormatter = new CLIPlayerInfoFormatter();
        this.isGameRunning = false;
    }

    @Override
    public void displayMessage(String message) {
        outputHandler.println(message);
    }

    @Override
    public void displayGameState(GameState gameState) {
        outputHandler.clear();
        outputHandler.println(gameRenderer.renderGame(gameState));
        outputHandler.println(playerInfoFormatter.formatPlayerInfo(gameState.getCurrentPlayer()));
    }

    /**
     * Mostra il menu di selezione del circuito.
     */
    public void showCircuitSelection() {
        outputHandler.println("\nSeleziona il circuito:");
        outputHandler.println("1. Circuito 1");
        outputHandler.println("2. Circuito 2");
        outputHandler.println("3. Circuito 3");
    }

    /**
     * Mostra il menu principale del gioco.
     */
    public void showGameMenu() {
        outputHandler.println("\nMenu di gioco:");
        outputHandler.println("1. Avvia simulazione automatica");
        outputHandler.println("2. Avanza di un turno");
        outputHandler.println("3. Esci");
    }

    /**
     * Legge una linea di input dalla console.
     * 
     * @return La stringa letta o null se si verifica un errore
     */
    public String readLine() {
        try {
            return scanner.nextLine();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Mostra un messaggio di errore.
     * 
     * @param message Il messaggio di errore da mostrare
     */
    public void showError(String message) {
        outputHandler.printError(message);
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }
}