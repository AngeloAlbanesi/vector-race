package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * Abstract base class representing a player in the Vector Race game.
 * This class provides the common functionality for both human and bot players,
 * managing their state and movement in the game.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Position and velocity tracking</li>
 *   <li>Movement history recording</li>
 *   <li>Checkpoint progression tracking</li>
 *   <li>Visual identification (name and color)</li>
 * </ul>
 * 
 * <p>The class follows these design principles:
 * <ul>
 *   <li>Immutable player identity (name and color)</li>
 *   <li>Protected state for subclass access</li>
 *   <li>Template method pattern for movement decisions</li>
 * </ul>
 */
public abstract class Player {
    protected final String name;
    protected final Color color;
    protected Position position;
    protected Vector velocity;
    protected final List<Position> movementHistory;
    protected int nextCheckpointIndex = 1;

    /**
     * Creates a new player with specified identity and starting position.
     *
     * @param name The player's name for identification.
     * @param color The player's color for visual representation.
     * @param startPosition The player's initial position on the track.
     */
    public Player(String name, Color color, Position startPosition) {
        this.name = name;
        this.color = color;
        this.position = startPosition;
        this.velocity = new Vector(0, 0);
        this.movementHistory = new ArrayList<>();
        this.movementHistory.add(startPosition);
    }

    /**
     * Gets the player's name.
     *
     * @return The player's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the player's color.
     *
     * @return The color used to represent this player.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Gets the player's current position.
     *
     * @return The current position on the track.
     */
    public Position getPosition() {
        return this.position;
    }

    /**
     * Gets the player's current velocity vector.
     *
     * @return The current velocity vector.
     */
    public Vector getVelocity() {
        return this.velocity;
    }

    /**
     * Gets the index of the next checkpoint to reach.
     *
     * @return The index of the next checkpoint.
     */
    public int getNextCheckpointIndex() {
        return this.nextCheckpointIndex;
    }

    /**
     * Advances to the next checkpoint in sequence.
     * Called when a checkpoint is successfully reached.
     */
    public void incrementCheckpointIndex() {
        this.nextCheckpointIndex++;
    }

    /**
     * Determines the next acceleration vector for the player's movement.
     * This is the core strategy method that must be implemented by specific
     * player types (human or bot) to determine their movement behavior.
     *
     * @param gameState The current state of the game.
     * @return The acceleration vector to apply for the next move.
     */
    public abstract Vector getNextAcceleration(GameState gameState);

    /**
     * Updates the player's position and records it in movement history.
     * This method maintains a complete record of the player's path through
     * the race track.
     *
     * @param newPosition The new position to move to.
     */
    public void updatePosition(Position newPosition) {
        this.position = newPosition;
        this.movementHistory.add(newPosition);
    }

    /**
     * Updates the player's velocity vector.
     * This represents the current movement momentum of the player.
     *
     * @param newVelocity The new velocity vector.
     */
    public void updateVelocity(Vector newVelocity) {
        this.velocity = newVelocity;
    }

    /**
     * Resets the player's velocity to zero.
     * Used when the player collides with walls or other players.
     */
    public void resetVelocity() {
        this.velocity = new Vector(0, 0);
    }

    @Override
    public String toString() {
        return this.name + " in " + this.position + " con velocità " + this.velocity;
    }
}