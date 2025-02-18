package it.unicam.cs.mdp.vectorrace.view.cli;

import it.unicam.cs.mdp.vectorrace.controller.IGameController;
import it.unicam.cs.mdp.vectorrace.view.CLIView;

/**
 * Manages the game menu and user interaction in the CLI.
 */
public class CLIMenuManager {
    private final CLIView view;
    private final IGameController controller;
    private final SimulationRunner simulationRunner;

    /**
     * Constructor for the {@code CLIMenuManager} class.
     *
     * @param view       The CLI view to use for interacting with the user.
     * @param controller The game controller to manage the game state.
     */
    public CLIMenuManager(CLIView view, IGameController controller) {
        this.view = view;
        this.controller = controller;
        this.simulationRunner = new SimulationRunner(controller, view);
    }

    /**
     * Runs the main loop of the game menu.
     */
    public void run() {
        boolean running = true;
        while (running) {
            view.showGameMenu();
            String input = view.readLine();
            running = processChoice(input);
        }
        System.exit(0); // Terminate the program when the menu loop ends
    }

    private boolean processChoice(String input) {
        try {
            int choice = Integer.parseInt(input);
            return switch (choice) {
                case 1 -> handleSimulation();
                case 2 -> handleSingleTurn();
                case 3 -> false;
                default -> {
                    view.showError("Scelta non valida. Riprova.");
                    yield true;
                }
            };
        } catch (NumberFormatException e) {
            view.showError("Input non valido. Inserisci un numero tra 1 e 2.");
            return true;
        }
    }

    private boolean handleSimulation() {
        simulationRunner.start();
        return !controller.getGameState().isFinished();
    }

    private boolean handleSingleTurn() {
        if (controller.getGameState().isFinished()) {
            view.displayMessage("La gara è già terminata!");
            return false;
        }
        controller.advanceTurn();
        return !controller.getGameState().isFinished();
    }
}