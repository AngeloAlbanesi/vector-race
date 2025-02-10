package it.unicam.cs.mdp.vectorrace;

import it.unicam.cs.mdp.vectorrace.controller.GameController;
import it.unicam.cs.mdp.vectorrace.model.GameState;
import it.unicam.cs.mdp.vectorrace.view.CLIView;
import it.unicam.cs.mdp.vectorrace.view.CircuitSelectionView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Paths;
import java.io.Console;
import java.util.Scanner;

/**
 * Punto di ingresso dell'applicazione.
 *
 * Se viene passato l'argomento "gui" viene usata l'interfaccia JavaFX,
 * altrimenti la CLI.
 */
public class Main extends Application {

    private static final String CIRCUIT_DIR = "src/main/resources/circuits";
    private static final String PLAYERS_FILE = "src/main/resources/players/players.txt";

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            launch(args);
        } else {
            startCLI();
        }
    }

    private static void startCLI() {
        CLIView cliView = new CLIView();
        String circuitPath = null;
        String playerFile = PLAYERS_FILE;

        Console console = System.console();
        if (console != null) {
            while (circuitPath == null) {
                cliView.showCircuitSelection();
                String input = console.readLine();
                circuitPath = handleCircuitSelection(input, cliView);
            }

            try {
                // Inizializza subito il gioco dopo la selezione del circuito
                GameState gameState = GameController.initializeGame(circuitPath, playerFile);
                GameController controller = new GameController(gameState, cliView);

                // Mostra lo stato iniziale e il menu
                cliView.displayGameState(gameState);

                boolean running = true;
                while (running) {
                    cliView.showGameMenu(); // Mostra il menu immediatamente
                    String input = console.readLine();
                    running = handleGameMenuChoice(input, controller, cliView);
                }
            } catch (Exception e) {
                System.err.println("Errore durante l'inizializzazione del gioco: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            try (Scanner scanner = new Scanner(System.in)) {
                while (circuitPath == null) {
                    cliView.showCircuitSelection();
                    if (scanner.hasNextLine()) {
                        String input = scanner.nextLine();
                        circuitPath = handleCircuitSelection(input, cliView);
                    } else {
                        System.err.println("Errore: impossibile leggere l'input. Utilizzo circuito di default (1)");
                        circuitPath = Paths.get(CIRCUIT_DIR, "circuit1.txt").toString();
                        break;
                    }
                }

                try {
                    // Inizializza subito il gioco dopo la selezione del circuito
                    GameState gameState = GameController.initializeGame(circuitPath, playerFile);
                    GameController controller = new GameController(gameState, cliView);

                    // Mostra lo stato iniziale e il menu
                    cliView.displayGameState(gameState);

                    boolean running = true;
                    while (running && scanner.hasNextLine()) {
                        cliView.showGameMenu(); // Mostra il menu immediatamente
                        String input = scanner.nextLine();
                        running = handleGameMenuChoice(input, controller, cliView);
                    }
                } catch (Exception e) {
                    System.err.println("Errore durante l'inizializzazione del gioco: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static String handleCircuitSelection(String input, CLIView cliView) {
        try {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    return Paths.get(CIRCUIT_DIR, "circuit1.txt").toString();
                case 2:
                    return Paths.get(CIRCUIT_DIR, "circuit2.txt").toString();
                case 3:
                    return Paths.get(CIRCUIT_DIR, "circuit3.txt").toString();
                default:
                    System.out.println("Scelta non valida. Riprova.");
                    return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero tra 1 e 3.");
            return null;
        }
    }

    private static boolean handleGameMenuChoice(String input, GameController controller, CLIView cliView) {
        try {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1: // Avvia simulazione automatica
                    runSimulation(controller, cliView);
                    // Se il gioco è finito, termina
                    return !controller.getGameState().isFinished();
                case 2: // Avanza di un turno
                    if (!controller.getGameState().isFinished()) {
                        controller.advanceTurn();
                        // Se il gioco è finito dopo questo turno, termina
                        return !controller.getGameState().isFinished();
                    } else {
                        System.out.println("La gara è già terminata!");
                        return false;
                    }
                case 3: // Esci
                    return false;
                default:
                    System.out.println("Scelta non valida. Riprova.");
                    return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero tra 1 e 3.");
            return true;
        }
    }

    private static void runSimulation(GameController controller, CLIView view) {
        view.setGameRunning(true);
        Thread simulationThread = new Thread(() -> {
            while (view.isGameRunning() && !controller.getGameState().isFinished()) {
                controller.advanceTurn();
                try {
                    Thread.sleep(1000); // Pausa di 1 secondo tra i turni
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            view.setGameRunning(false);
        });
        simulationThread.start();

        // Aspetta che la simulazione termini
        try {
            simulationThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Vector Race");
            CircuitSelectionView selectionView = new CircuitSelectionView(primaryStage);
            selectionView.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore durante l'avvio dell'interfaccia grafica: " + e.getMessage());
        }
    }
}