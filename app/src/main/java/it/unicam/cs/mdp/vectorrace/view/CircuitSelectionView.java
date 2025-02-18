package it.unicam.cs.mdp.vectorrace.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import it.unicam.cs.mdp.vectorrace.controller.GameControllerFactory;
import it.unicam.cs.mdp.vectorrace.controller.IGameController;
import it.unicam.cs.mdp.vectorrace.config.GUIConfig;

import java.nio.file.Paths;

/**
 * The {@code CircuitSelectionView} class represents the view for selecting the game circuit.
 * It allows the user to choose from different available circuits through a graphical interface.
 */
public class CircuitSelectionView {
    private Stage stage;
    private final String BASE_PATH = "/Users/angeloalbanesi/Università/Programmazione avanzata Loreti/vector-race/app";
    private final String playerFile;
    private final GameControllerFactory controllerFactory;

    /**
     * Constructor for the {@code CircuitSelectionView} class.
     *
     * @param stage The JavaFX stage on which the view will be displayed.
     */
    public CircuitSelectionView(Stage stage) {
        this.stage = stage;
        this.playerFile = GUIConfig.PLAYERS_FILE.toString();
        this.controllerFactory = new GameControllerFactory();
    }

    /**
     * Shows the view for circuit selection.
     * Creates a layout with buttons for each available circuit.
     */
    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Text title = new Text("Seleziona il circuito");
        title.setStyle("-fx-font-size: 24px;");

        Button circuit1 = new Button("Circuito 1");
        Button circuit2 = new Button("Circuito 2");
        Button circuit3 = new Button("Circuito 3");

        String circuit1Path = Paths.get(this.BASE_PATH, "src/main/resources/circuits/circuit1.txt").toString();
        String circuit2Path = Paths.get(this.BASE_PATH, "src/main/resources/circuits/circuit2.txt").toString();
        String circuit3Path = Paths.get(this.BASE_PATH, "src/main/resources/circuits/circuit3.txt").toString();

        circuit1.setOnAction(e -> this.loadCircuit(circuit1Path));
        circuit2.setOnAction(e -> this.loadCircuit(circuit2Path));
        circuit3.setOnAction(e -> this.loadCircuit(circuit3Path));

        root.getChildren().addAll(title, circuit1, circuit2, circuit3);

        Scene scene = new Scene(root, 400, 300);
        this.stage.setTitle("Vector Race - Selezione Circuito");
        this.stage.setScene(scene);
        this.stage.show();
    }

    /**
     * Loads the specified circuit and starts the game view.
     *
     * @param circuitPath The path of the circuit file to load.
     */
    private void loadCircuit(String circuitPath) {
        try {
            System.out.println("Caricamento circuito: " + circuitPath);
            System.out.println("File giocatori: " + this.playerFile);

            GUIView gameView = new GUIView();
            IGameController controller = controllerFactory.createController(circuitPath, this.playerFile, gameView);
            gameView.setController(controller);
            gameView.start(this.stage);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento del circuito: " + e.getMessage());
            e.printStackTrace();
        }
    }
}