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
 * Gestisce gli input utente e gli eventi UI per il gioco.
 * Segue il Single Responsibility Principle gestendo solo gli input utente.
 */
public class GUIInputHandler {
    private final Timeline timeline;
    private final Label statusLabel;
    private boolean isPaused = true;
    private final Consumer<Void> onUpdate;

    public GUIInputHandler(Timeline timeline, Label statusLabel, Consumer<Void> onUpdate) {
        this.timeline = timeline;
        this.statusLabel = statusLabel;
        this.onUpdate = onUpdate;
    }

    /**
     * Configura i controlli di gioco (pulsanti e slider).
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
     * Gestisce il click del mouse sulla griglia di gioco.
     */
    public void handleGridClick(double mouseX, double mouseY, int cellSize, GameState gameState, Set<Position> validMoves) {
        if (!isPaused && !(gameState.getCurrentPlayer() instanceof HumanPlayer)) {
            return;
        }

        HumanPlayer humanPlayer = (HumanPlayer) gameState.getCurrentPlayer();
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

    public boolean isPaused() {
        return isPaused;
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }
}