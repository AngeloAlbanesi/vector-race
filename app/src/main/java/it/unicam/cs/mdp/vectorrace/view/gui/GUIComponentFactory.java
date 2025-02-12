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
 * Factory per la creazione dei componenti dell'interfaccia grafica.
 * Centralizza la creazione di tutti gli elementi UI per una migliore
 * manutenibilità.
 */
public class GUIComponentFactory {

    /**
     * Crea il layout principale dell'applicazione.
     */
    public BorderPane createMainLayout() {
        return new BorderPane();
    }

    /**
     * Crea il canvas per il rendering del gioco.
     */
    public Canvas createGameCanvas(Track track, int cellSize) {
        return new Canvas(
                track.getWidth() * cellSize,
                track.getHeight() * cellSize);
    }

    /**
     * Crea il pannello dei controlli di gioco.
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
     * Crea il pannello dei pulsanti di controllo.
     */
    public HBox createButtonPanel(Button startButton, Button pauseButton,
            Button stepButton, Button exitButton) {
        return new HBox(GUIConstants.CONTROL_SPACING,
                startButton, pauseButton, stepButton, exitButton);
    }

    /**
     * Crea il controllo della velocità.
     */
    public HBox createSpeedControl(Slider speedSlider) {
        Label speedLabel = new Label("Velocità:");
        return new HBox(GUIConstants.CONTROL_SPACING, speedLabel, speedSlider);
    }

    /**
     * Crea il pulsante di avvio.
     */
    public Button createStartButton() {
        return new Button("Avvia");
    }

    /**
     * Crea il pulsante di pausa.
     */
    public Button createPauseButton() {
        return new Button("Pausa");
    }

    /**
     * Crea il pulsante per l'avanzamento di un turno.
     */
    public Button createStepButton() {
        return new Button("Passo");
    }

    /**
     * Crea il pulsante di uscita.
     */
    public Button createExitButton() {
        return new Button("Termina gara");
    }

    /**
     * Crea lo slider per il controllo della velocità.
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
     * Crea l'etichetta per lo stato del gioco.
     */
    public Label createStatusLabel() {
        return new Label("Pronto");
    }
}