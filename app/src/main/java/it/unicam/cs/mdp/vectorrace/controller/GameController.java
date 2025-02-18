package it.unicam.cs.mdp.vectorrace.controller;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.game.TurnManager;
import it.unicam.cs.mdp.vectorrace.view.GameView;

/**
 * The main controller for the Vector Race game, responsible for coordinating game components.
 * This class implements the Mediator pattern to facilitate communication between the model and view components.
 * It manages the game state and controls the flow of the game through turns.
 *
 * <p>The controller acts as a bridge between:
 * <ul>
 *   <li>The game state (model) which contains all game data</li>
 *   <li>The view which presents the game to the user</li>
 *   <li>The turn manager which handles game progression</li>
 * </ul>
 */
public class GameController implements IGameController {
    private final GameState gameState;
    private final GameView view;
    private final TurnManager turnManager;

    /**
     * Constructs a new GameController with the specified game state and view.
     * Initializes the turn manager with a new movement manager to handle player movements.
     *
     * @param gameState The state of the game containing track and player information.
     * @param view The view interface responsible for displaying the game.
     */
    public GameController(GameState gameState, GameView view) {
        this.gameState = gameState;
        this.view = view;
        this.turnManager = new TurnManager(gameState, new MovementManager(), view);
    }

    /**
     * Advances the game to the next turn.
     * Delegates the turn progression to the turn manager, which handles:
     * <ul>
     *   <li>Player movement validation</li>
     *   <li>Game state updates</li>
     *   <li>View updates</li>
     * </ul>
     */
    public void advanceTurn() {
        turnManager.advanceTurn();
    }

    /**
     * Gets the current state of the game.
     * Provides access to all game-related information including:
     * <ul>
     *   <li>Track layout</li>
     *   <li>Player positions</li>
     *   <li>Game progress</li>
     * </ul>
     *
     * @return The current game state.
     */
    public GameState getGameState() {
        return gameState;
    }
}
