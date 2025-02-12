package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.HumanPlayer;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
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

import java.util.HashSet;
import java.util.Set;

/**
 * Interfaccia grafica realizzata con JavaFX per la simulazione del gioco.
 */
public class GUIView extends Application {

    private static GameState gameState;
    private Canvas canvas;
    private Timeline timeline;
    private Label statusLabel;
    private boolean isPaused = true;
    private int cellSize = 15; // Ridotto da 30 a 15 per celle più piccole
    private final MovementManager movementManager = new MovementManager();
    private Set<Position> validMoves = new HashSet<>();

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
        this.cellSize = (int) Math.min(Math.min(widthRatio, heightRatio), 30);

        BorderPane root = new BorderPane();
        this.canvas = new Canvas(track.getWidth() * this.cellSize, track.getHeight() * this.cellSize);
        root.setCenter(this.canvas);

        // Pannello di controllo
        Button startButton = new Button("Avvia");
        Button pauseButton = new Button("Pausa");
        Button stepButton = new Button("Passo");
        Button exitButton = new Button("Termina gara");
        this.statusLabel = new Label("Pronto");

        // Slider per la velocità
        Slider speedSlider = new Slider(0.1, 2.0, 1.0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);

        // Layout migliorato per i controlli
        VBox controlsBox = new VBox(10);
        HBox buttons = new HBox(10, startButton, pauseButton, stepButton, exitButton);
        HBox sliderBox = new HBox(10, new Label("Velocità:"), speedSlider);
        controlsBox.getChildren().addAll(buttons, sliderBox, this.statusLabel);
        controlsBox.setPadding(new Insets(10));
        root.setBottom(controlsBox);

        // Eventi di controllo
        startButton.setOnAction(e -> {
            this.isPaused = false;
            this.statusLabel.setText("In esecuzione");
            this.timeline.play();
        });
        pauseButton.setOnAction(e -> {
            this.isPaused = true;
            this.statusLabel.setText("In pausa");
            this.timeline.pause();
        });
        stepButton.setOnAction(e -> {
            if (this.isPaused) {
                this.advanceTurn();
                this.draw();
            }
        });
        exitButton.setOnAction(e -> {
            this.timeline.stop();
            Platform.exit();
        });

        // Timeline per aggiornamenti automatici (inizialmente ferma)
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1.0 / speedSlider.getValue()), e -> {
            if (!this.isPaused && !gameState.isFinished()) {
                this.advanceTurn();
            }
        }));
        this.timeline.setCycleCount(Timeline.INDEFINITE);

        // Aggiorna la velocità in base allo slider
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            this.timeline.setRate(newVal.doubleValue());
        });

        // Aggiungi event handler per il mouse sul canvas
        this.canvas.setOnMouseClicked(e -> this.handleMouseClick(e.getX(), e.getY()));

        // Inizializza le mosse valide se il primo giocatore è umano
        if (gameState.getCurrentPlayer() instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) gameState.getCurrentPlayer();
            this.validMoves = humanPlayer.calculateValidMoves(gameState);
        }

        Scene scene = new Scene(root);
        primaryStage.setTitle("Vector Rally - Simulazione");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        this.draw(); // Disegna lo stato iniziale
    }

    private void handleMouseClick(double mouseX, double mouseY) {
        if (this.isPaused && gameState.getCurrentPlayer() instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) gameState.getCurrentPlayer();

            // Converti le coordinate del mouse in coordinate della griglia
            int gridX = (int) (mouseX / this.cellSize);
            int gridY = (int) (mouseY / this.cellSize);
            Position clickedPos = new Position(gridX, gridY);

            // Se la posizione cliccata è una mossa valida
            if (this.validMoves.contains(clickedPos)) {
                // Calcola l'accelerazione necessaria per raggiungere la posizione
                Position currentPos = humanPlayer.getPosition();
                Vector currentVel = humanPlayer.getVelocity();
                Vector newVel = new Vector(clickedPos.getX() - currentPos.getX(),
                        clickedPos.getY() - currentPos.getY())
                        .subtract(currentVel);

                humanPlayer.setSelectedAcceleration(newVel);

                // Esegui la mossa
                this.advanceTurn();
            }
        }
    }

    /**
     * Avanza un turno (un giocatore).
     */
    private void advanceTurn() {
        Player currentPlayer = gameState.getCurrentPlayer();
        Track track = gameState.getTrack();

        // Ottieni l'accelerazione dal giocatore (BFS/A* o umano)
        Vector acceleration = currentPlayer.getNextAcceleration(gameState);
        if (acceleration == null) {
            this.statusLabel.setText(currentPlayer.getName() + " non ha fornito un'accelerazione valida.");
            acceleration = new Vector(0, 0);
        }

        // Movimento valido su muri/giocatori fermi?
        if (!this.movementManager.validateMove(currentPlayer, acceleration, gameState)) {
            this.statusLabel.setText(
                    currentPlayer.getName() + " ha colliso con un muro o giocatore fermo! Velocità resettata.");
            currentPlayer.resetVelocity();
        } else {
            // Movimento ok su muri, costruiamo la nuova posizione
            Vector newVelocity = currentPlayer.getVelocity().add(acceleration);
            Position newPosition = currentPlayer.getPosition().move(newVelocity);

            // Controllo "anti-sovrapposizione" con altri giocatori GIA' mossi
            if (this.isPositionOccupiedByOtherPlayer(newPosition, currentPlayer)) {
                // Se la cella è già presa, il giocatore resta fermo
                this.statusLabel.setText(
                        currentPlayer.getName() + " ha trovato la cella occupata da un altro giocatore, resta fermo!");
                currentPlayer.resetVelocity();
            } else {
                // Non è occupata: aggiorniamo posizione e velocità
                currentPlayer.updatePosition(newPosition);
                currentPlayer.updateVelocity(newVelocity);

                // Check se ha raggiunto FINISH
                CellType currentCell = track.getCell(newPosition.getX(), newPosition.getY());
                if (currentCell == CellType.FINISH) {
                    gameState.setFinished(true);
                    gameState.setWinner(currentPlayer);
                    this.statusLabel.setText("Il Giocatore " + currentPlayer.getName() + " ha vinto la gara!");
                    this.timeline.stop();

                    // Mostra dialog di vittoria e chiudi l'applicazione
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Fine della gara");
                        alert.setHeaderText("Vittoria!");
                        alert.setContentText("Il Giocatore " + currentPlayer.getName() + " ha vinto la gara!");
                        alert.showAndWait().ifPresent(response -> Platform.exit());
                    });
                    return;
                }
            }
        }

        // Controlla se ha terminato la gara
        if (gameState.checkFinish(currentPlayer)) {
            gameState.setFinished(true);
            gameState.setWinner(currentPlayer);
            this.statusLabel.setText("Il Giocatore " + currentPlayer.getName() + " ha vinto la gara!");
            this.timeline.stop();

            // Mostra dialog di vittoria e chiudi l'applicazione
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fine della gara");
                alert.setHeaderText("Vittoria!");
                alert.setContentText("Il Giocatore " + currentPlayer.getName() + " ha vinto la gara!");
                alert.showAndWait().ifPresent(response -> Platform.exit());
            });
            return;
        }

        // Passa turno
        gameState.nextTurn();

        // Aggiorna le mosse valide se il prossimo giocatore è umano
        if (gameState.getCurrentPlayer() instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) gameState.getCurrentPlayer();
            this.validMoves = humanPlayer.calculateValidMoves(gameState);
        } else {
            this.validMoves.clear();
        }

        this.draw();
    }

    /**
     * Ritorna true se 'position' è già occupata da un altro giocatore.
     */
    private boolean isPositionOccupiedByOtherPlayer(Position position, Player currentPlayer) {
        for (Player otherPlayer : gameState.getPlayers()) {
            if (otherPlayer != currentPlayer && otherPlayer.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Disegna circuito e giocatori sul canvas.
     */
    private void draw() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        Track track = gameState.getTrack();

        // Disegna il circuito
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                CellType cell = track.getCell(x, y);
                Color fill;
                Position currentPos = new Position(x, y);

                // Evidenzia le mosse valide per il giocatore umano
                if (!this.validMoves.isEmpty() && this.validMoves.contains(currentPos)) {
                    fill = Color.LIGHTGREEN;
                } else {
                    switch (cell) {
                        case WALL:
                            fill = Color.BLACK;
                            break;
                        case ROAD:
                        case CHECKPOINT:
                            fill = Color.LIGHTGRAY;
                            break;
                        case START:
                            fill = Color.LIGHTGREEN;
                            break;
                        case FINISH:
                            fill = Color.LIGHTCORAL;
                            break;
                        default:
                            fill = Color.WHITE;
                            break;
                    }
                }

                gc.setFill(fill);
                gc.fillRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
            }
        }

        // Disegna i giocatori
        for (Player player : gameState.getPlayers()) {
            Position pos = player.getPosition();
            Color pColor = Color.rgb(
                    player.getColor().getRed(),
                    player.getColor().getGreen(),
                    player.getColor().getBlue());
            gc.setFill(pColor);
            gc.fillOval(
                    pos.getX() * this.cellSize + 2,
                    pos.getY() * this.cellSize + 2,
                    this.cellSize - 4,
                    this.cellSize - 4);
        }
    }
}
