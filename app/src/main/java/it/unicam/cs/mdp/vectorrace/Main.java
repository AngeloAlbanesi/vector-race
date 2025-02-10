package it.unicam.cs.mdp.vectorrace;

import it.unicam.cs.mdp.vectorrace.controller.GameController;
import it.unicam.cs.mdp.vectorrace.model.GameState;
import it.unicam.cs.mdp.vectorrace.view.CLIView;
import it.unicam.cs.mdp.vectorrace.view.CircuitSelectionView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione.
 *
 * Se viene passato l'argomento "gui" viene usata l'interfaccia JavaFX,
 * altrimenti la CLI.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String[] params = getParameters().getRaw().toArray(new String[0]);
        if (params.length > 0 && params[0].equalsIgnoreCase("gui")) {
            try {
                primaryStage.setTitle("Vector Race");
                CircuitSelectionView selectionView = new CircuitSelectionView(primaryStage);
                selectionView.show();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Errore durante l'avvio dell'interfaccia grafica: " + e.getMessage());
            }
        } else {
            // CLI mode
            try {
                String trackFile = "src/main/resources/circuits/circuit2.txt";
                String playerFile = "src/main/resources/players/players.txt";
                GameState gameState = GameController.initializeGame(trackFile, playerFile);
                CLIView cliView = new CLIView();
                GameController controller = new GameController(gameState, cliView);
                controller.startGame();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Errore durante l'inizializzazione: " + e.getMessage());
            }
        }
    }
}