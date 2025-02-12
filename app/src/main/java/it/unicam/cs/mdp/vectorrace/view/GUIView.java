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
    private Canvas canvas;
    private Label statusLabel;
    private Button startButton;
    private Button pauseButton;
    private Button stepButton;
    private Button exitButton;
    private Slider speedSlider;

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

    /**
     * Inizializza tutti i componenti dell'interfaccia grafica.
     * Questo metodo è stato suddiviso in metodi helper più piccoli per migliorare la manutenibilità.
     */
    private void initializeComponents(Stage primaryStage) {
        int cellSize = calculateOptimalCellSize();
        BorderPane root = setupUIComponents(cellSize);
        initializeGameLoop(cellSize);
        initializeManagers();
        configureEventHandlers(cellSize);
        setupStage(primaryStage, root);
        // Renderizza solo lo stato iniziale senza avanzare il turno
        renderer.render(gameState, stateManager.getValidMoves());
    }

    /**
     * Crea e configura tutti i componenti dell'interfaccia utente.
     */
    private BorderPane setupUIComponents(int cellSize) {
        GUIComponentFactory componentFactory = new GUIComponentFactory();
        BorderPane root = componentFactory.createMainLayout();
        
        // Configurazione canvas e renderer
        canvas = componentFactory.createGameCanvas(gameState.getTrack(), cellSize);
        root.setCenter(canvas);
        this.renderer = new GUIRenderer(canvas, cellSize);

        // Creazione controlli UI
        statusLabel = componentFactory.createStatusLabel();
        startButton = componentFactory.createStartButton();
        pauseButton = componentFactory.createPauseButton();
        stepButton = componentFactory.createStepButton();
        exitButton = componentFactory.createExitButton();
        speedSlider = componentFactory.createSpeedSlider();

        // Configurazione pannello controlli
        root.setBottom(componentFactory.createControlPanel(
            startButton, pauseButton, stepButton, exitButton, speedSlider, statusLabel
        ));

        return root;
    }

    /**
     * Inizializza il game loop con la velocità di default.
     */
    private void initializeGameLoop(int cellSize) {
        this.gameLoop = createGameLoop(GUIConstants.DEFAULT_SPEED);
    }

    /**
     * Inizializza i gestori per lo stato del gioco e l'input.
     */
    private void initializeManagers() {
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
    }

    /**
     * Configura gli handler degli eventi per i controlli UI.
     */
    private void configureEventHandlers(int cellSize) {
        inputHandler.setupGameControls(
            startButton, pauseButton, stepButton, exitButton, speedSlider
        );

        canvas.setOnMouseClicked(e -> inputHandler.handleGridClick(
            e.getX(), e.getY(), cellSize, gameState, stateManager.getValidMoves()
        ));
    }

    /**
     * Configura e mostra la finestra principale del gioco.
     */
    private void setupStage(Stage primaryStage, BorderPane root) {
        Scene scene = new Scene(root);
        primaryStage.setTitle("Vector Rally - Simulazione");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
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
