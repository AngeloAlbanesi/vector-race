package it.unicam.cs.mdp.vectorrace.model.game;

import java.util.List;
import java.util.ArrayList;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.validators.MoveValidator;
import it.unicam.cs.mdp.vectorrace.model.game.validators.WallCollisionValidator;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Manages movement validation in the Vector Race game using a chain of validators.
 * This class implements a flexible validation system that can accommodate different
 * types of movement rules and restrictions.
 *
 * <p>Key features:
 * <ul>
 *   <li>Validator chain pattern for movement validation</li>
 *   <li>Dependency injection support for validators</li>
 *   <li>Specialized validation for pathfinding operations</li>
 * </ul>
 *
 * <p>The validation process ensures that:
 * <ul>
 *   <li>Movements don't pass through walls</li>
 *   <li>Players don't collide with each other</li>
 *   <li>All game-specific movement rules are followed</li>
 * </ul>
 */
public class MovementManager {
    
    private final List<MoveValidator> validators;
    private final WallCollisionValidator wallValidator;
    
    /**
     * Creates a default MovementManager with standard validators.
     * Uses the MovementManagerFactory to initialize the default set of validators,
     * ensuring consistent validation behavior across the game.
     */
    public MovementManager() {
        MovementManager defaultManager = MovementManagerFactory.createDefault();
        this.validators = defaultManager.validators;
        // WallCollisionValidator is always the first validator by convention
        this.wallValidator = (WallCollisionValidator) validators.get(0);
    }
    
    /**
     * Creates a MovementManager with a custom set of validators.
     * Implements Dependency Injection pattern for maximum flexibility in
     * configuring movement validation rules.
     *
     * @param validators The list of validators to use. The first validator must be
     *                  an instance of {@link WallCollisionValidator}.
     * @throws IllegalArgumentException if the first validator is not a WallCollisionValidator.
     */
    public MovementManager(List<MoveValidator> validators) {
        if (!(validators.get(0) instanceof WallCollisionValidator)) {
            throw new IllegalArgumentException("Il primo validatore deve essere WallCollisionValidator");
        }
        this.validators = new ArrayList<>(validators);
        this.wallValidator = (WallCollisionValidator) validators.get(0);
    }
    
    /**
     * Validates a player's proposed movement based on acceleration.
     * This method:
     * <ol>
     *   <li>Calculates the new velocity by applying acceleration</li>
     *   <li>Determines the resulting position</li>
     *   <li>Validates the movement using all configured validators</li>
     * </ol>
     *
     * @param player The player attempting to move.
     * @param acceleration The acceleration vector to apply.
     * @param gameState The current state of the game.
     * @return true if the move is valid according to all validators, false otherwise.
     */
    public boolean validateMove(Player player, Vector acceleration, GameState gameState) {
        Vector newVelocity = player.getVelocity().add(acceleration);
        Position newPosition = player.getPosition().move(newVelocity);
        
        return isMovementValid(player.getPosition(), newPosition, player, gameState);
    }
    
    /**
     * Validates a movement by checking all configured validators.
     * The movement is considered valid only if all validators approve it.
     * This implements a chain of responsibility pattern where each validator
     * must approve the movement.
     *
     * @param start The starting position.
     * @param end The ending position.
     * @param player The player attempting the move.
     * @param gameState The current state of the game.
     * @return true if all validators approve the move, false otherwise.
     */
    private boolean isMovementValid(Position start, Position end, Player player, GameState gameState) {
        return validators.stream()
                .allMatch(validator -> validator.isValidMove(start, end, player, gameState));
    }
    
    /**
     * Performs simplified movement validation for pathfinding operations.
     * This method uses only the wall collision validator to check if a movement
     * is physically possible, ignoring other game rules. This is primarily used
     * by AI pathfinding algorithms.
     *
     * @param start The starting position.
     * @param velocity The velocity vector to apply.
     * @param track The track to validate against.
     * @return true if the move doesn't collide with walls, false otherwise.
     */
    public boolean validateMoveTemp(Position start, Vector velocity, Track track) {
        Position end = start.add(velocity);
        return wallValidator.isValidMove(start, end, null, new GameState(track, true));
    }
}
