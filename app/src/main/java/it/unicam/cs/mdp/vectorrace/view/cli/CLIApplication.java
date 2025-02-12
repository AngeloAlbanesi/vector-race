package it.unicam.cs.mdp.vectorrace.view.cli;

import it.unicam.cs.mdp.vectorrace.config.CLIConfig;
import it.unicam.cs.mdp.vectorrace.controller.GameController;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.view.CLIView;

/**
 * Gestisce l'avvio e l'esecuzione dell'applicazione in modalità CLI.
 * Coordina l'interazione tra view, controller e gestione degli input utente.
 */
public class CLIApplication {
    private final CLIView view;
    private GameController controller;

    public CLIApplication(CLIView view) {
        this.view = view;
    }

    /**
     * Avvia l'applicazione in modalità CLI.
     * Gestisce la selezione del circuito, l'inizializzazione del gioco
     * e l'esecuzione del menu principale.
     */
    public void start() {
        try {
            String circuitPath = selectCircuit();
            if (circuitPath != null) {
                initializeGame(circuitPath);
                runGameLoop();
            }
        } catch (Exception e) {
            handleFatalError("Application startup", e);
        }
    }

    private String selectCircuit() {
        String circuitPath = null;
        CircuitSelector selector = new CircuitSelector(view);
        
        while (circuitPath == null) {
            view.showCircuitSelection();
            String input = readInput();
            if (input == null) break;
            circuitPath = selector.handleCircuitSelection(input);
        }
        
        return circuitPath;
    }

    private void initializeGame(String circuitPath) throws Exception {
        GameState gameState = GameController.initializeGame(circuitPath, CLIConfig.PLAYERS_FILE.toString());
        controller = new GameController(gameState, view);
        view.displayGameState(gameState);
    }

    private void runGameLoop() {
        CLIMenuManager menuManager = new CLIMenuManager(view, controller);
        menuManager.run();
    }

    private String readInput() {
        if (System.console() != null) {
            return System.console().readLine();
        } else {
            return view.readLine();
        }
    }

    private void handleFatalError(String context, Exception e) {
        System.err.println("Errore fatale durante " + context + ": " + e.getMessage());
        e.printStackTrace();
        System.exit(1);
    }
}