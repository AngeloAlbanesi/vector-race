package it.unicam.cs.mdp.vectorrace.view.cli;

import it.unicam.cs.mdp.vectorrace.controller.IGameController;
import it.unicam.cs.mdp.vectorrace.view.CLIView;

/**
 * Gestisce l'esecuzione automatica della simulazione del gioco.
 */
public class SimulationRunner {
    private final IGameController controller;
    private final CLIView view;
    private Thread simulationThread;
    private volatile boolean running;

    public SimulationRunner(IGameController controller, CLIView view) {
        this.controller = controller;
        this.view = view;
    }

    /**
     * Avvia la simulazione automatica del gioco.
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
            
            // Se il gioco è finito per una vittoria, termina il programma
            if (controller.getGameState().isFinished() && controller.getGameState().getWinner() != null) {
                Thread.sleep(2000); // Attende 2 secondi per mostrare il messaggio di vittoria
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
     * Interrompe la simulazione.
     */
    public void stop() {
        running = false;
        view.setGameRunning(false);
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }
    }
}