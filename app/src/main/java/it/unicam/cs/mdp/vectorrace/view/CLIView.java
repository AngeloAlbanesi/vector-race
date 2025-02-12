package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.view.output.IOutputHandler;
import it.unicam.cs.mdp.vectorrace.view.output.ConsoleOutputHandler;
import it.unicam.cs.mdp.vectorrace.view.renderer.IGameRenderer;
import it.unicam.cs.mdp.vectorrace.view.renderer.CLIGameRenderer;
import it.unicam.cs.mdp.vectorrace.view.renderer.IPlayerInfoFormatter;
import it.unicam.cs.mdp.vectorrace.view.renderer.CLIPlayerInfoFormatter;

/**
 * Implementazione CLI dell'interfaccia di visualizzazione del gioco.
 * Utilizza il pattern Strategy per delegare le responsabilità di rendering e output
 * a componenti specializzati.
 */
public class CLIView implements GameView, CLISpecific {
    private final IOutputHandler outputHandler;
    private final IGameRenderer gameRenderer;
    private boolean isGameRunning;

    /**
     * Costruisce una nuova istanza di CLIView con i componenti necessari.
     */
    public CLIView() {
        this.outputHandler = new ConsoleOutputHandler();
        IPlayerInfoFormatter playerInfoFormatter = new CLIPlayerInfoFormatter();
        this.gameRenderer = new CLIGameRenderer(playerInfoFormatter);
        this.isGameRunning = false;
    }

    @Override
    public void displayMessage(String message) {
        outputHandler.displayLine(message);
    }

    @Override
    public void showCircuitSelection() {
        outputHandler.displayLine("\n=== Vector Race - Selezione Circuito ===");
        outputHandler.displayLine("1. Circuito 1");
        outputHandler.displayLine("2. Circuito 2");
        outputHandler.displayLine("3. Circuito 3");
        outputHandler.display("Seleziona un circuito (1-3): ");
    }

    @Override
    public void showGameMenu() {
        outputHandler.displayLine("\n=== Menu di Gioco ===");
        outputHandler.displayLine("1. Avvia simulazione");
        outputHandler.displayLine("2. Avanza di un turno");
        outputHandler.display("Seleziona un'opzione (1-2): ");
    }

    @Override
    public void displayGameState(GameState gameState) {
        // Pulisce lo schermo prima di mostrare il nuovo stato
        outputHandler.clearScreen();
        
        // Utilizza il renderer per generare la rappresentazione del gioco
        String gameStateRepresentation = gameRenderer.renderGameState(gameState);
        outputHandler.displayLine(gameStateRepresentation);
        
        // Se il gioco è finito, gestisce la terminazione
        if (gameState.isFinished() && gameState.getWinner() != null) {
            handleGameEnd();
        } else {
            // Altrimenti mostra il menu di gioco
            showGameMenu();
        }
        
        // Aggiunge una riga vuota per separare lo stato dal menu
        outputHandler.displayLine("");
    }

    /**
     * Gestisce la terminazione del gioco.
     */
    private void handleGameEnd() {
        // Aspetta un secondo per assicurarsi che l'utente veda il messaggio
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Forza la terminazione del programma
        System.exit(0);
    }

    @Override
    public void setGameRunning(boolean running) {
        this.isGameRunning = running;
    }

    @Override
    public boolean isGameRunning() {
        return this.isGameRunning;
    }
}