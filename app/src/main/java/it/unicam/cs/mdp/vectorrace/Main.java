package it.unicam.cs.mdp.vectorrace;

import it.unicam.cs.mdp.vectorrace.controller.GameController;
import it.unicam.cs.mdp.vectorrace.model.GameState;
import it.unicam.cs.mdp.vectorrace.view.CLIView;
import it.unicam.cs.mdp.vectorrace.view.GUIView;
import javafx.application.Application;

/**
 * Punto di ingresso dell'applicazione.
 *
 * Se viene passato l'argomento "gui" viene usata l'interfaccia JavaFX,
 * altrimenti la CLI.
 */
public class Main {

    public static void main(String[] args) {
        boolean useGUI = false;
        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            useGUI = true;
        }

        // Percorsi corretti per i file di configurazione usando il classloader
        String trackFile = "src/main/resources/circuits/circuit2.txt";
        String playerFile = "src/main/resources/players/players.txt";

        try {
            GameState gameState = GameController.initializeGame(trackFile, playerFile);
            if (useGUI) {
                GUIView.setGameState(gameState);
                Application.launch(GUIView.class, args);
            } else {
                CLIView cliView = new CLIView();
                GameController controller = new GameController(gameState, cliView);
                controller.startGame();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore durante l'inizializzazione: " + e.getMessage());
        }
    }
}