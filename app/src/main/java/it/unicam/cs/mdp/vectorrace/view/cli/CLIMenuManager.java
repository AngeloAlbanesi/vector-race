package it.unicam.cs.mdp.vectorrace.view.cli;

import it.unicam.cs.mdp.vectorrace.controller.GameController;
import it.unicam.cs.mdp.vectorrace.view.CLIView;

/**
 * Gestisce il menu di gioco e l'interazione con l'utente nella CLI.
 */
public class CLIMenuManager {
    private final CLIView view;
    private final GameController controller;
    private final SimulationRunner simulationRunner;

    public CLIMenuManager(CLIView view, GameController controller) {
        this.view = view;
        this.controller = controller;
        this.simulationRunner = new SimulationRunner(controller, view);
    }

    /**
     * Esegue il loop principale del menu di gioco.
     */
    public void run() {
        boolean running = true;
        while(running) {
            view.showGameMenu();
            String input = view.readLine();
            running = processChoice(input);
        }
    }

    private boolean processChoice(String input) {
        try {
            int choice = Integer.parseInt(input);
            return switch(choice) {
                case 1 -> handleSimulation();
                case 2 -> handleSingleTurn();
                case 3 -> false;
                default -> {
                    view.showError("Scelta non valida. Riprova.");
                    yield true;
                }
            };
        } catch(NumberFormatException e) {
            view.showError("Input non valido. Inserisci un numero tra 1 e 3.");
            return true;
        }
    }

    private boolean handleSimulation() {
        simulationRunner.start();
        return !controller.getGameState().isFinished();
    }

    private boolean handleSingleTurn() {
        if(controller.getGameState().isFinished()) {
            view.displayMessage("La gara è già terminata!");
            return false;
        }
        controller.advanceTurn();
        return !controller.getGameState().isFinished();
    }
}