package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.CellType;
import it.unicam.cs.mdp.vectorrace.model.GameState;
import it.unicam.cs.mdp.vectorrace.model.Player;
import it.unicam.cs.mdp.vectorrace.model.Position;
import it.unicam.cs.mdp.vectorrace.model.Track;
import it.unicam.cs.mdp.vectorrace.model.Vector;
import it.unicam.cs.mdp.vectorrace.model.MovementManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Interfaccia grafica realizzata con JavaFX per la simulazione del gioco. I
 * checkpoint sono rappresentati da numeri: ogni volta che un bot raggiunge il
 * checkpoint atteso (ossia, la cella CHECKPOINT il cui ordine corrisponde a
 * getNextCheckpointIndex()), il bot aggiorna il target al checkpoint successivo
 * e infine punta al traguardo.
 */
public class GUIView extends Application {

    private static GameState gameState;
    private Canvas canvas;
    private Timeline timeline;
    private Label statusLabel;
    private boolean isPaused = true;
    private int cellSize = 15; // Ridotto da 30 a 15 per celle più piccole
    private final MovementManager movementManager = new MovementManager();

    /**
     * Imposta lo stato di gioco da visualizzare.
     *
     * @param state Stato di gioco
     */
    public static void setGameState(GameState state) {
        gameState = state;
    }

    @Override
    public void start(Stage primaryStage) {
        if (gameState == null) {
            System.err.println("GameState non inizializzato.");
            Platform.exit();
            return;
        }

        // Calcola le dimensioni della finestra in base allo schermo
        Screen screen = Screen.getPrimary();
        double maxWidth = screen.getVisualBounds().getWidth() * 0.8;
        double maxHeight = screen.getVisualBounds().getHeight() * 0.8;

        // Calcola il cellSize ottimale
        Track track = gameState.getTrack();
        double widthRatio = maxWidth / track.getWidth();
        double heightRatio = maxHeight / track.getHeight();
        cellSize = (int) Math.min(Math.min(widthRatio, heightRatio), 30);

        BorderPane root = new BorderPane();
        canvas = new Canvas(track.getWidth() * cellSize, track.getHeight() * cellSize);
        root.setCenter(canvas);

        // Pannello di controllo
        Button startButton = new Button("Avvia");
        Button pauseButton = new Button("Pausa");
        Button stepButton = new Button("Passo");
        statusLabel = new Label("Pronto");

        // Slider per la velocità
        Slider speedSlider = new Slider(0.1, 2.0, 1.0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);

        // Layout migliorato per i controlli
        VBox controlsBox = new VBox(10);
        HBox buttons = new HBox(10, startButton, pauseButton, stepButton);
        HBox sliderBox = new HBox(10, new Label("Velocità:"), speedSlider);
        controlsBox.getChildren().addAll(buttons, sliderBox, statusLabel);
        controlsBox.setPadding(new Insets(10));
        root.setBottom(controlsBox);

        // Eventi di controllo
        startButton.setOnAction(e -> {
            isPaused = false;
            statusLabel.setText("In esecuzione");
            timeline.play();
        });
        pauseButton.setOnAction(e -> {
            isPaused = true;
            statusLabel.setText("In pausa");
            timeline.pause();
        });
        stepButton.setOnAction(e -> {
            if (isPaused) {
                advanceTurn();
                draw();
            }
        });

        // Timeline per aggiornamenti automatici (inizialmente ferma)
        timeline = new Timeline(new KeyFrame(Duration.seconds(1.0 / speedSlider.getValue()), e -> {
            if (!isPaused && !gameState.isFinished()) {
                advanceTurn();
                draw();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Aggiorna la velocità in base allo slider
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            timeline.setRate(newVal.doubleValue());
        });

        Scene scene = new Scene(root);
        primaryStage.setTitle("Vector Rally - Simulazione");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        draw(); // Disegna lo stato iniziale
    }

    /**
     * Avanza un turno.
     */
    private void advanceTurn() {
        Player currentPlayer = gameState.getCurrentPlayer();
        Track track = gameState.getTrack();

        // Ottieni l'accelerazione
        Vector acceleration = currentPlayer.getNextAcceleration(gameState);
        if (acceleration == null) {
            statusLabel.setText(currentPlayer.getName() + " non ha fornito un'accelerazione valida.");
            acceleration = new Vector(0, 0);
        }

        // Prova ad applicare il movimento usando MovementManager
        if (!movementManager.validateMove(currentPlayer, acceleration, gameState)) {
            statusLabel.setText(currentPlayer.getName() + " ha colliso! Velocità resettata.");
            currentPlayer.resetVelocity();
        } else {
            // Il movimento è valido, aggiorna posizione e velocità
            Vector newVelocity = currentPlayer.getVelocity().add(acceleration);
            Position newPosition = currentPlayer.getPosition().move(newVelocity);
            currentPlayer.updatePosition(newPosition);
            currentPlayer.updateVelocity(newVelocity);

            // Verifica se ha raggiunto il traguardo
            CellType currentCell = track.getCell(newPosition.getX(), newPosition.getY());
            if (currentCell == CellType.FINISH) {
                statusLabel.setText(currentPlayer.getName() + " ha raggiunto il traguardo!");
            }
        }

        if (gameState.checkFinish(currentPlayer)) {
            gameState.setFinished(true);
            gameState.setWinner(currentPlayer);
            statusLabel.setText("Vincitore: " + currentPlayer.getName());
            timeline.stop(); // Ferma l'animazione
            return; // Esce per evitare nextTurn() dopo la fine
        }
        gameState.nextTurn();
    }

    private boolean isPositionOccupiedByOtherPlayer(Position position, Player currentPlayer) {
        for (Player otherPlayer : gameState.getPlayers()) {
            if (otherPlayer != currentPlayer && otherPlayer.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Disegna il circuito e i giocatori sul canvas.
     */
    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Track track = gameState.getTrack();

        // Disegna il circuito
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                CellType cell = track.getCell(x, y);
                Color fill;
                switch (cell) {
                    case WALL:
                        fill = Color.BLACK;
                        break;
                    case ROAD:
                        fill = Color.LIGHTGRAY;
                        break;
                    case START:
                        fill = Color.LIGHTGREEN;
                        break;
                    case FINISH:
                        fill = Color.LIGHTCORAL;
                        break;
                    case CHECKPOINT:
                        fill = Color.LIGHTBLUE;
                        break;
                    default:
                        fill = Color.WHITE;
                        break;
                }
                gc.setFill(fill);
                gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

                // Disegna il contorno delle celle
                gc.setStroke(Color.BLACK);
                gc.strokeRect(x * cellSize, y * cellSize, cellSize, cellSize);

                // Se è un checkpoint, mostra il numero
                if (cell == CellType.CHECKPOINT) {
                    gc.setFill(Color.BLACK);
                    int checkpointNum = getCheckpointNumber(track, x, y);
                    gc.fillText(String.valueOf(checkpointNum),
                            x * cellSize + cellSize / 3,
                            y * cellSize + cellSize / 2);
                }
            }
        }

        // Disegna i giocatori
        for (Player player : gameState.getPlayers()) {
            Position pos = player.getPosition();
            Color pColor = Color.rgb(player.getColor().getRed(),
                    player.getColor().getGreen(),
                    player.getColor().getBlue());
            gc.setFill(pColor);
            gc.fillOval(pos.getX() * cellSize + 2,
                    pos.getY() * cellSize + 2,
                    cellSize - 4, cellSize - 4);

            // Mostra il prossimo checkpoint del giocatore
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(player.getNextCheckpointIndex()),
                    pos.getX() * cellSize + cellSize / 3,
                    pos.getY() * cellSize + cellSize / 2);
        }
    }

    private int getCheckpointNumber(Track track, int x, int y) {
        // Ordina i checkpoint da sinistra a destra
        int checkpointIndex = 0;
        boolean found = false;

        // Prima riga (checkpoint 1)
        for (int col = 0; col < track.getWidth() && !found; col++) {
            for (int row = 0; row < track.getHeight(); row++) {
                if (track.getCell(col, row) == CellType.CHECKPOINT) {
                    if (col == x && row == y) {
                        return checkpointIndex + 1;
                    }
                    checkpointIndex++;
                    break;
                }
            }
        }

        // Seconda riga (checkpoint 2)
        for (int col = 0; col < track.getWidth() && !found; col++) {
            for (int row = track.getHeight() - 1; row >= 0; row--) {
                if (track.getCell(col, row) == CellType.CHECKPOINT) {
                    if (col == x && row == y) {
                        return checkpointIndex + 1;
                    }
                    checkpointIndex++;
                    break;
                }
            }
        }

        // Terza riga (checkpoint 3)
        for (int col = track.getWidth() - 1; col >= 0 && !found; col--) {
            for (int row = track.getHeight() - 1; row >= 0; row--) {
                if (track.getCell(col, row) == CellType.CHECKPOINT) {
                    if (col == x && row == y) {
                        return checkpointIndex + 1;
                    }
                    checkpointIndex++;
                    break;
                }
            }
        }

        // Ultima riga (checkpoint 4)
        for (int col = track.getWidth() - 1; col >= 0 && !found; col--) {
            for (int row = 0; row < track.getHeight(); row++) {
                if (track.getCell(col, row) == CellType.CHECKPOINT) {
                    if (col == x && row == y) {
                        return checkpointIndex + 1;
                    }
                    checkpointIndex++;
                    break;
                }
            }
        }

        return checkpointIndex + 1;
    }
}