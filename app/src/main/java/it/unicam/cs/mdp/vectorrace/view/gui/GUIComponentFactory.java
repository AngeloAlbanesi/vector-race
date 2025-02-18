package it.unicam.cs.mdp.vectorrace.view.gui;

import it.unicam.cs.mdp.vectorrace.model.core.Track;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Factory for creating the components of the graphical interface.
 * Centralizes the creation of all UI elements for better
 * maintainability.
 */
public class GUIComponentFactory {

    /**
     * Creates the main layout of the application.
     *
     * @return The main layout of the application.
     */
    public BorderPane createMainLayout() {
        return new BorderPane();
    }

    /**
     * Creates the canvas for rendering the game.
     *
     * @param track    The game track.
     * @param cellSize The size of the cells in the game.
     * @return The canvas for rendering the game.
     */
    public Canvas createGameCanvas(Track track, int cellSize) {
        return new Canvas(
                track.getWidth() * cellSize,
                track.getHeight() * cellSize);
    }

    /**
     * Creates the control panel for the game.
     *
     * @param startButton The start button.
     * @param pauseButton The pause button.
     * @param stepButton  The step button.
     * @param exitButton  The exit button.
     * @param speedSlider The speed slider.
     * @param statusLabel The status label.
     * @return The control panel for the game.
     */
    public VBox createControlPanel(Button startButton, Button pauseButton,
            Button stepButton, Button exitButton,
            Slider speedSlider, Label statusLabel) {
        HBox buttons = createButtonPanel(startButton, pauseButton, stepButton, exitButton);
        HBox sliderBox = createSpeedControl(speedSlider);

        VBox controlsBox = new VBox(GUIConstants.CONTROL_SPACING);
        controlsBox.setPadding(new Insets(GUIConstants.CONTROL_PADDING));
        controlsBox.getChildren().addAll(buttons, sliderBox, statusLabel);

        return controlsBox;
    }

    /**
     * Creates the button panel for the controls.
     *
     * @param startButton The start button.
     * @param pauseButton The pause button.
     * @param stepButton  The step button.
     * @param exitButton  The exit button.
     * @return The button panel for the controls.
     */
    public HBox createButtonPanel(Button startButton, Button pauseButton,
            Button stepButton, Button exitButton) {
        return new HBox(GUIConstants.CONTROL_SPACING,
                startButton, pauseButton, stepButton, exitButton);
    }

    /**
     * Creates the speed control.
     *
     * @param speedSlider The speed slider.
     * @return The speed control.
     */
    public HBox createSpeedControl(Slider speedSlider) {
        Label speedLabel = new Label("Velocità:");
        return new HBox(GUIConstants.CONTROL_SPACING, speedLabel, speedSlider);
    }

    /**
     * Creates the start button.
     *
     * @return The start button.
     */
    public Button createStartButton() {
        return new Button("Avvia");
    }

    /**
     * Creates the pause button.
     *
     * @return The pause button.
     */
    public Button createPauseButton() {
        return new Button("Pausa");
    }

    /**
     * Creates the step button.
     *
     * @return The step button.
     */
    public Button createStepButton() {
        return new Button("Passo");
    }

    /**
     * Creates the exit button.
     *
     * @return The exit button.
     */
    public Button createExitButton() {
        return new Button("Termina gara");
    }

    /**
     * Creates the slider for the speed control.
     *
     * @return The slider for the speed control.
     */
    public Slider createSpeedSlider() {
        Slider slider = new Slider(
                GUIConstants.MIN_SPEED,
                GUIConstants.MAX_SPEED,
                GUIConstants.DEFAULT_SPEED);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        return slider;
    }

    /**
     * Creates the label for the game state.
     *
     * @return The label for the game state.
     */
    public Label createStatusLabel() {
        return new Label("Pronto");
    }
}