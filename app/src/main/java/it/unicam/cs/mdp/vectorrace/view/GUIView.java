package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.view.gui.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Interfaccia grafica principale del gioco.
 * Coordina i vari componenti dell'interfaccia e del gioco.
 */
public class GUIView extends Application {
    private static GameState gameState;
    private GUIRenderer renderer;
    private GUIInputHandler inputHandler;
    private GUIGameStateManager stateManager;
    private Timeline gameLoop;

    /**
     * Imposta lo stato di gioco da visualizzare.
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

        initializeComponents(primaryStage);
    }

    private void initializeComponents(Stage primaryStage) {
        // Calcola le dimensioni ottimali della finestra
        int cellSize = calculateOptimalCellSize();
        
        // Crea i componenti UI
        GUIComponentFactory componentFactory = new GUIComponentFactory();
        BorderPane root = componentFactory.createMainLayout();
        
        // Crea il canvas e il renderer
        Canvas canvas = componentFactory.createGameCanvas(gameState.getTrack(), cellSize);
        root.setCenter(canvas);
        this.renderer = new GUIRenderer(canvas, cellSize);

        // Crea i controlli
        Label statusLabel = componentFactory.createStatusLabel();
        Button startButton = componentFactory.createStartButton();
        Button pauseButton = componentFactory.createPauseButton();
        Button stepButton = componentFactory.createStepButton();
        Button exitButton = componentFactory.createExitButton();
        Slider speedSlider = componentFactory.createSpeedSlider();

        // Crea il pannello dei controlli
        root.setBottom(componentFactory.createControlPanel(
            startButton, pauseButton, stepButton, exitButton, speedSlider, statusLabel
        ));

        // Inizializza il game loop
        this.gameLoop = createGameLoop(speedSlider.getValue());

        // Inizializza i gestori
        this.stateManager = new GUIGameStateManager(
            gameState,
            new MovementManager(),
            statusLabel::setText
        );

        this.inputHandler = new GUIInputHandler(
            gameLoop,
            statusLabel,
            v -> updateGame()
        );

        // Configura gli eventi
        inputHandler.setupGameControls(
            startButton, pauseButton, stepButton, exitButton, speedSlider
        );
        canvas.setOnMouseClicked(e -> inputHandler.handleGridClick(
            e.getX(), e.getY(), cellSize, gameState, stateManager.getValidMoves()
        ));

        // Configura e mostra la finestra
        Scene scene = new Scene(root);
        primaryStage.setTitle("Vector Rally - Simulazione");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        // Renderizza lo stato iniziale
        updateGame();
    }

    private Timeline createGameLoop(double initialSpeed) {
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(1.0 / initialSpeed),
            e -> {
                if (!inputHandler.isPaused() && !gameState.isFinished()) {
                    updateGame();
                }
            }
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }

    private int calculateOptimalCellSize() {
        Screen screen = Screen.getPrimary();
        double maxWidth = screen.getVisualBounds().getWidth() * GUIConstants.WINDOW_SCALE_FACTOR;
        double maxHeight = screen.getVisualBounds().getHeight() * GUIConstants.WINDOW_SCALE_FACTOR;
        
        double widthRatio = maxWidth / gameState.getTrack().getWidth();
        double heightRatio = maxHeight / gameState.getTrack().getHeight();
        
        return (int) Math.min(
            Math.min(widthRatio, heightRatio),
            GUIConstants.MAX_CELL_SIZE
        );
    }

    private void updateGame() {
        stateManager.advanceTurn();
        renderer.render(gameState, stateManager.getValidMoves());
    }
}
