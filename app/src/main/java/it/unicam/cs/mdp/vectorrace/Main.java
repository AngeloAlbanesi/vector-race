package it.unicam.cs.mdp.vectorrace;

import it.unicam.cs.mdp.vectorrace.view.CLIView;
import it.unicam.cs.mdp.vectorrace.view.CircuitSelectionView;
import it.unicam.cs.mdp.vectorrace.view.cli.CLIApplication;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point of the application.
 * If the argument "gui" is passed, the JavaFX interface is used,
 * otherwise the CLI is used.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Vector Race");
            CircuitSelectionView selectionView = new CircuitSelectionView(primaryStage);
            selectionView.show();
        } catch (Exception e) {
            System.err.println("Errore durante l'avvio dell'interfaccia grafica: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main method of the application.
     *
     * @param args Command line arguments. If the first argument is "gui", the GUI is launched.
     *             Otherwise, the CLI is launched.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            launch(args);
        } else {
            new CLIApplication(new CLIView()).start();
        }
    }
}