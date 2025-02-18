package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;

/**
 * Represents a human-controlled player in the Vector Race game.
 * This class manages user input through the JavaFX GUI interface and handles
 * movement validation and visualization of available moves.
 *
 * <p>Key features:
 * <ul>
 *   <li>Interactive move selection through GUI</li>
 *   <li>Real-time move validation</li>
 *   <li>Visual feedback for valid moves</li>
 *   <li>Movement state management</li>
 * </ul>
 *
 * <p>The class maintains:
 * <ul>
 *   <li>Currently selected acceleration</li>
 *   <li>Set of valid moves for the current turn</li>
 *   <li>Visual highlighting for available moves</li>
 * </ul>
 */
public class HumanPlayer extends Player {
    private final MovementManager movementManager;
    private Vector selectedAcceleration;
    private Set<Position> validMoves;
    private static final Color HIGHLIGHT_COLOR = Color.decode("#90EE90");

    /**
     * Creates a new human player with specified attributes.
     * Initializes movement management and validation components.
     *
     * @param name The player's identifier name.
     * @param color The player's display color.
     * @param startPosition The player's initial position on the track.
     */
    public HumanPlayer(String name, Color color, Position startPosition) {
        super(name, color, startPosition);
        this.movementManager = new MovementManager();
        this.validMoves = new HashSet<>();
        this.selectedAcceleration = null;
    }

    /**
     * Calculates all valid moves available to the player.
     * This method:
     * <ol>
     *   <li>Clears previous valid moves</li>
     *   <li>Checks all possible acceleration vectors (-1,0,1 for both x and y)</li>
     *   <li>Validates each potential move using the movement manager</li>
     *   <li>Stores valid destination positions</li>
     * </ol>
     * 
     * @param gameState The current state of the game.
     * @return Set of all valid positions the player can move to.
     */
    public Set<Position> calculateValidMoves(GameState gameState) {
        this.validMoves.clear();
        // Check all 8 possible directions (-1,0,1 for x and y)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Vector acceleration = new Vector(dx, dy);
                Vector newVelocity = this.getVelocity().add(acceleration);
                Position newPosition = this.getPosition().move(newVelocity);

                if (this.movementManager.validateMove(this, acceleration, gameState)) {
                    this.validMoves.add(newPosition);
                }
            }
        }
        return this.validMoves;
    }

    /**
     * Checks if a given position represents a valid move.
     * Used by the GUI to determine which cells should be highlighted.
     *
     * @param position The position to check.
     * @return true if the position is a valid move destination.
     */
    public boolean isValidMove(Position position) {
        return this.validMoves.contains(position);
    }

    /**
     * Sets the acceleration vector selected by the user through the GUI.
     * This method is called when the user clicks on a valid destination cell.
     *
     * @param acceleration The selected acceleration vector.
     */
    public void setSelectedAcceleration(Vector acceleration) {
        this.selectedAcceleration = acceleration;
    }

    /**
     * Gets the color used to highlight valid moves in the GUI.
     * This color provides visual feedback for available move options.
     *
     * @return The highlight color for valid moves.
     */
    public Color getHighlightColor() {
        return HIGHLIGHT_COLOR;
    }

    @Override
    public Vector getNextAcceleration(GameState gameState) {
        return this.selectedAcceleration != null ? this.selectedAcceleration : new Vector(0, 0);
    }

    /**
     * Resets the player's movement selection state.
     * This method:
     * <ul>
     *   <li>Clears the selected acceleration</li>
     *   <li>Clears the set of valid moves</li>
     * </ul>
     * Called at the end of each turn or when the user cancels a move.
     */
    public void resetSelection() {
        this.selectedAcceleration = null;
        this.validMoves.clear();
    }
}