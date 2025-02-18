package it.unicam.cs.mdp.vectorrace.model.game;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.view.GameView;

/**
 * Manages the turn-based gameplay mechanics in the Vector Race game.
 * This class coordinates player movements, collisions, and victory conditions,
 * acting as the core engine for game progression.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Turn progression and management</li>
 *   <li>Movement validation and execution</li>
 *   <li>Collision detection and handling</li>
 *   <li>Victory condition verification</li>
 * </ul>
 */
public class TurnManager {
    private final GameState gameState;
    private final MovementManager movementManager;
    private final GameView view;

    /**
     * Creates a new TurnManager with the specified components.
     *
     * @param gameState The current state of the game.
     * @param movementManager The manager handling movement validation.
     * @param view The view component for displaying game updates.
     */
    public TurnManager(GameState gameState, MovementManager movementManager, GameView view) {
        this.gameState = gameState;
        this.movementManager = movementManager;
        this.view = view;
    }

    /**
     * Advances the game by one turn.
     * This method orchestrates the complete turn sequence:
     * <ol>
     *   <li>Gets and validates player acceleration</li>
     *   <li>Processes player movement</li>
     *   <li>Checks for victory conditions</li>
     *   <li>Updates game state and view</li>
     * </ol>
     */
    public void advanceTurn() {
        Player currentPlayer = this.gameState.getCurrentPlayer();

        Vector acceleration = this.getAndValidateAcceleration(currentPlayer);

        if (this.processMovement(currentPlayer, acceleration)) {
            if (this.checkVictoryConditions(currentPlayer)) {
                this.endGame(currentPlayer);
                return;
            }
        }

        this.gameState.nextTurn();
        this.view.displayGameState(this.gameState);
    }

    /**
     * Gets and validates the acceleration vector from a player.
     * If the player provides an invalid acceleration, returns a zero vector.
     *
     * @param player The player whose acceleration to get.
     * @return The validated acceleration vector, or zero vector if invalid.
     */
    private Vector getAndValidateAcceleration(Player player) {
        Vector acceleration = player.getNextAcceleration(this.gameState);
        if (acceleration == null) {
            this.view.displayMessage(player.getName() + " non ha fornito un'accelerazione valida.");
            return new Vector(0, 0);
        }
        return acceleration;
    }

    /**
     * Processes a player's movement based on their acceleration.
     * This method handles:
     * <ul>
     *   <li>Movement validation</li>
     *   <li>Collision detection</li>
     *   <li>Position updates</li>
     * </ul>
     *
     * @param player The player attempting to move.
     * @param acceleration The acceleration vector to apply.
     * @return true if the movement was successful, false if blocked or invalid.
     */
    private boolean processMovement(Player player, Vector acceleration) {
        if (!this.movementManager.validateMove(player, acceleration, this.gameState)) {
            this.handleCollision(player);
            return false;
        }

        Vector newVelocity = player.getVelocity().add(acceleration);
        Position newPosition = player.getPosition().move(newVelocity);

        if (this.isPositionOccupied(newPosition, player)) {
            this.handleOccupiedPosition(player);
            return false;
        }

        player.updatePosition(newPosition);
        player.updateVelocity(newVelocity);
        return true;
    }

    /**
     * Checks if a position is occupied by another player.
     * Used for collision detection between players.
     *
     * @param position The position to check.
     * @param currentPlayer The player making the move (excluded from check).
     * @return true if the position is occupied by another player.
     */
    private boolean isPositionOccupied(Position position, Player currentPlayer) {
        return this.gameState.getPlayers().stream()
                .anyMatch(other -> other != currentPlayer && other.getPosition().equals(position));
    }

    /**
     * Handles a collision with a wall or obstacle.
     * Resets the player's velocity and displays appropriate message.
     *
     * @param player The player who collided.
     */
    private void handleCollision(Player player) {
        this.view.displayMessage(player.getName() + " ha colliso con un muro o giocatore fermo! Velocità resettata.");
        player.resetVelocity();
    }

    /**
     * Handles the case where a player attempts to move to an occupied position.
     * Resets the player's velocity and displays appropriate message.
     *
     * @param player The player who encountered an occupied position.
     */
    private void handleOccupiedPosition(Player player) {
        this.view
                .displayMessage(player.getName() + " ha trovato la cella occupata da un altro giocatore, resta fermo!");
        player.resetVelocity();
    }

    /**
     * Checks if a player has met the victory conditions.
     * Victory is achieved by reaching a finish line cell.
     *
     * @param player The player to check for victory.
     * @return true if the player has won, false otherwise.
     */
    private boolean checkVictoryConditions(Player player) {
        Position pos = player.getPosition();
        return this.gameState.getTrack().getCell(pos.getX(), pos.getY()) == CellType.FINISH ||
                this.gameState.checkFinish(player);
    }

    /**
     * Handles the end of the game when a player wins.
     * Updates game state and displays victory message.
     *
     * @param winner The player who won the game.
     */
    private void endGame(Player winner) {
        this.gameState.setFinished(true);
        this.gameState.setWinner(winner);
        this.view.displayMessage("Il Giocatore " + winner.getName() + " ha vinto la gara!");
        this.view.displayGameState(this.gameState);
    }
}