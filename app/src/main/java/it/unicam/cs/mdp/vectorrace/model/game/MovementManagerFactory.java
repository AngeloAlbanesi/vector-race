package it.unicam.cs.mdp.vectorrace.model.game;

import java.util.Arrays;

import it.unicam.cs.mdp.vectorrace.model.game.validators.WallCollisionValidator;
import it.unicam.cs.mdp.vectorrace.model.game.validators.PlayerCollisionValidator;

/**
 * Factory responsible for creating properly configured instances of {@link MovementManager}.
 * This class implements the Factory Method pattern to encapsulate the complex creation
 * and configuration of movement managers and their validators.
 * 
 * <p>The factory ensures that:
 * <ul>
 *   <li>Validators are initialized in the correct order</li>
 *   <li>All necessary validators are included</li>
 *   <li>The validation chain is properly configured</li>
 * </ul>
 * 
 * <p>The default configuration includes:
 * <ol>
 *   <li>{@link WallCollisionValidator} - Ensures movements don't pass through walls</li>
 *   <li>{@link PlayerCollisionValidator} - Prevents collisions between players</li>
 * </ol>
 * 
 * <p>Note: The order of validators is significant, as the {@link WallCollisionValidator}
 * must always be the first validator in the chain.
 */
public class MovementManagerFactory {
    
    /**
     * Creates a new {@link MovementManager} instance with the standard set of validators.
     * This method provides a consistent way to create movement managers with the
     * default game rules and collision detection.
     * 
     * <p>The created manager will enforce:
     * <ul>
     *   <li>Wall collision detection and prevention</li>
     *   <li>Player-to-player collision detection and prevention</li>
     * </ul>
     *
     * @return A fully configured {@link MovementManager} instance with standard validators.
     */
    public static MovementManager createDefault() {
        return new MovementManager(Arrays.asList(
            new WallCollisionValidator(),   // Must be first
            new PlayerCollisionValidator()  // Additional collision checks
        ));
    }
}