package it.unicam.cs.mdp.vectorrace.view.cli;

import it.unicam.cs.mdp.vectorrace.controller.IGameController;
import it.unicam.cs.mdp.vectorrace.view.CLIView;

/**
 * Manages the automatic execution of the game simulation.
 */
public class SimulationRunner {
    private final IGameController controller;
    private final CLIView view;
    private Thread simulationThread;
    private volatile boolean running;

    /**
     * Constructor for the {@code SimulationRunner} class.
     *
     * @param controller The game controller to use for running the simulation.
     * @param view       The CLI view to use for displaying the game state.
     */
    public SimulationRunner(IGameController controller, CLIView view) {
        this.controller = controller;
        this.view = view;
    }

    /**
     * Starts the automatic simulation of the game.
     */
    public void start() {
        running = true;
        view.setGameRunning(true);
        simulationThread = new Thread(this::runSimulation);
        simulationThread.start();
        waitForCompletion();
    }

    private void runSimulation() {
        try {
            while (running && !controller.getGameState().isFinished()) {
                controller.advanceTurn();
                Thread.sleep(1000); // Pausa di 1 secondo tra i turni
            }

            // If the game is finished due to a victory, terminate the program
            if (controller.getGameState().isFinished() && controller.getGameState().getWinner() != null) {
                Thread.sleep(1000); 
                System.out.println("Il giocatore " + controller.getGameState().getWinner().getName() + " ha vinto!");
                System.exit(0);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            view.setGameRunning(false);
            running = false;
        }
    }

    private void waitForCompletion() {
        try {
            simulationThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stop();
        }
    }

    /**
     * Stops the simulation.
     */
    public void stop() {
        running = false;
        view.setGameRunning(false);
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }
    }
}