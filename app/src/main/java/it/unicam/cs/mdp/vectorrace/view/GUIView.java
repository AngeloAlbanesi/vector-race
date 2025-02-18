package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.controller.IGameController;
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
 * The {@code GUIView} class extends {@code Application} and implements {@code GameView}
 * to provide the main graphical user interface for the Vector Race game.
 * It coordinates the various components of the user interface and manages the game loop.
 */
public class GUIView extends Application implements GameView {
    private IGameController controller;
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
     * Sets the game controller.
     *
     * @param controller The instance of {@code IGameController} to use.
     */
    public void setController(IGameController controller) {
        this.controller = controller;
    }

    /**
     * Displays the current state of the game on the graphical interface.
     *
     * @param state The game state to display.
     */
    @Override
    public void displayGameState(GameState state) {
        if (renderer != null) {
            renderer.render(state, stateManager != null ? stateManager.getValidMoves() : null);
        }
    }

    /**
     * Displays a status message on the graphical interface.
     *
     * @param message The message to display.
     */
    @Override
    public void displayMessage(String message) {
        if (statusLabel != null) {
            Platform.runLater(() -> statusLabel.setText(message));
        }
    }

    /**
     * The main method for starting the JavaFX application.
     * Initializes and configures the graphical interface.
     *
     * @param primaryStage The main stage of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        if (controller == null) {
            System.err.println("Controller non inizializzato.");
            Platform.exit();
            return;
        }

        initializeComponents(primaryStage);
    }

    /**
     * Initializes all components of the graphical interface.
     * This method has been divided into smaller helper methods to improve maintainability.
     *
     * @param primaryStage The main stage of the application.
     */
    private void initializeComponents(Stage primaryStage) {
        int cellSize = calculateOptimalCellSize();
        BorderPane root = setupUIComponents(cellSize);
        initializeGameLoop(cellSize);
        initializeManagers();
        configureEventHandlers(cellSize);
        setupStage(primaryStage, root);
        // Render only the initial state without advancing the turn
        renderer.render(controller.getGameState(), stateManager.getValidMoves());
    }

    /**
     * Creates and configures all components of the user interface.
     *
     * @param cellSize The size of the game cells.
     * @return The {@code BorderPane} containing all components of the user interface.
     */
    private BorderPane setupUIComponents(int cellSize) {
        GUIComponentFactory componentFactory = new GUIComponentFactory();
        BorderPane root = componentFactory.createMainLayout();

        // Configuration of the canvas and renderer
        canvas = componentFactory.createGameCanvas(controller.getGameState().getTrack(), cellSize);
        root.setCenter(canvas);
        this.renderer = new GUIRenderer(canvas, cellSize);

        // Creation of UI controls
        statusLabel = componentFactory.createStatusLabel();
        startButton = componentFactory.createStartButton();
        pauseButton = componentFactory.createPauseButton();
        stepButton = componentFactory.createStepButton();
        exitButton = componentFactory.createExitButton();
        speedSlider = componentFactory.createSpeedSlider();

        // Configuration of the control panel
        root.setBottom(componentFactory.createControlPanel(
            startButton, pauseButton, stepButton, exitButton, speedSlider, statusLabel
        ));

        return root;
    }

    /**
     * Initializes the game loop with the default speed.
     *
     * @param cellSize The size of the game cells.
     */
    private void initializeGameLoop(int cellSize) {
        this.gameLoop = createGameLoop(GUIConstants.DEFAULT_SPEED);
    }

    /**
     * Initializes the handlers for the game state and input.
     */
    private void initializeManagers() {
        this.stateManager = new GUIGameStateManager(
            controller.getGameState(),
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
     * Configures the event handlers for the UI controls.
     *
     * @param cellSize The size of the game cells.
     */
    private void configureEventHandlers(int cellSize) {
        inputHandler.setupGameControls(
            startButton, pauseButton, stepButton, exitButton, speedSlider
        );

        canvas.setOnMouseClicked(e -> inputHandler.handleGridClick(
            e.getX(), e.getY(), cellSize, controller.getGameState(), stateManager.getValidMoves()
        ));
    }

    /**
     * Configures and displays the main game window.
     *
     * @param primaryStage The main stage of the application.
     * @param root         The {@code BorderPane} root of the user interface.
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
                if (!inputHandler.isPaused() && !controller.getGameState().isFinished()) {
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

        double widthRatio = maxWidth / controller.getGameState().getTrack().getWidth();
        double heightRatio = maxHeight / controller.getGameState().getTrack().getHeight();

        return (int) Math.min(
            Math.min(widthRatio, heightRatio),
            GUIConstants.MAX_CELL_SIZE
        );
    }

    private void updateGame() {
        stateManager.advanceTurn();
        renderer.render(controller.getGameState(), stateManager.getValidMoves());
    }
}
