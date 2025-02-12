package it.unicam.cs.mdp.vectorrace;

import it.unicam.cs.mdp.vectorrace.view.CLIView;
import it.unicam.cs.mdp.vectorrace.view.CircuitSelectionView;
import it.unicam.cs.mdp.vectorrace.view.cli.CLIApplication;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione.
 * Se viene passato l'argomento "gui" viene usata l'interfaccia JavaFX,
 * altrimenti la CLI.
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

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            launch(args);
        } else {
            new CLIApplication(new CLIView()).start();
        }
    }
}