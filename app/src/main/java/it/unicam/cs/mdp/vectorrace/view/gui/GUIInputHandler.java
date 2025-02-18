package it.unicam.cs.mdp.vectorrace.view.gui;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.HumanPlayer;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.animation.Timeline;
import javafx.application.Platform;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Manages user inputs and UI events for the game.
 * Follows the Single Responsibility Principle by handling only user inputs.
 */
public class GUIInputHandler {
    private final Timeline timeline;
    private final Label statusLabel;
    private boolean isPaused = true;
    private final Consumer<Void> onUpdate;

    /**
     * Constructor for the {@code GUIInputHandler} class.
     *
     * @param timeline      The timeline for the game loop.
     * @param statusLabel   The label for displaying the status.
     * @param onUpdate      The callback to use for updating the game.
     */
    public GUIInputHandler(Timeline timeline, Label statusLabel, Consumer<Void> onUpdate) {
        this.timeline = timeline;
        this.statusLabel = statusLabel;
        this.onUpdate = onUpdate;
    }

    /**
     * Configures the game controls (buttons and slider).
     *
     * @param startButton The start button.
     * @param pauseButton The pause button.
     * @param stepButton  The step button.
     * @param exitButton  The exit button.
     * @param speedSlider The speed slider.
     */
    public void setupGameControls(Button startButton, Button pauseButton, Button stepButton, Button exitButton, Slider speedSlider) {
        startButton.setOnAction(e -> handleStartButton());
        pauseButton.setOnAction(e -> handlePauseButton());
        stepButton.setOnAction(e -> handleStepButton());
        exitButton.setOnAction(e -> handleExitButton());
        setupSpeedSlider(speedSlider);
    }

    private void handleStartButton() {
        isPaused = false;
        statusLabel.setText("In esecuzione");
        timeline.play();
    }

    private void handlePauseButton() {
        isPaused = true;
        statusLabel.setText("In pausa");
        timeline.pause();
    }

    private void handleStepButton() {
        if (isPaused) {
            onUpdate.accept(null);
        }
    }

    private void handleExitButton() {
        timeline.stop();
        Platform.exit();
    }

    private void setupSpeedSlider(Slider speedSlider) {
        speedSlider.setMin(GUIConstants.MIN_SPEED);
        speedSlider.setMax(GUIConstants.MAX_SPEED);
        speedSlider.setValue(GUIConstants.DEFAULT_SPEED);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) ->
            timeline.setRate(newVal.doubleValue())
        );
    }

    /**
     * Handles the mouse click on the game grid.
     *
     * @param mouseX      The x coordinate of the mouse click.
     * @param mouseY      The y coordinate of the mouse click.
     * @param cellSize    The size of the cells in the game.
     * @param gameState   The game state.
     * @param validMoves  The set of valid moves for the current player.
     */
    public void handleGridClick(double mouseX, double mouseY, int cellSize, GameState gameState, Set<Position> validMoves) {
        Player currentPlayer = gameState.getCurrentPlayer();

        // Verifica se il gioco è in pausa o se il giocatore non è umano
        if (!isPaused || !(currentPlayer instanceof HumanPlayer)) {
            return;
        }

        // Ora possiamo fare il cast in sicurezza
        HumanPlayer humanPlayer = (HumanPlayer) currentPlayer;
        Position clickedPos = new Position(
            (int) (mouseX / cellSize),
            (int) (mouseY / cellSize)
        );

        if (validMoves.contains(clickedPos)) {
            processValidMove(humanPlayer, clickedPos);
            onUpdate.accept(null);
        }
    }

    private void processValidMove(HumanPlayer player, Position targetPos) {
        Position currentPos = player.getPosition();
        Vector currentVel = player.getVelocity();
        Vector newVel = new Vector(
            targetPos.getX() - currentPos.getX(),
            targetPos.getY() - currentPos.getY()
        ).subtract(currentVel);

        player.setSelectedAcceleration(newVel);
    }

    /**
     * Returns whether the game is paused.
     *
     * @return {@code true} if the game is paused, {@code false} otherwise.
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Updates the status message.
     *
     * @param message The message to display.
     */
    public void updateStatus(String message) {
        statusLabel.setText(message);
    }
}