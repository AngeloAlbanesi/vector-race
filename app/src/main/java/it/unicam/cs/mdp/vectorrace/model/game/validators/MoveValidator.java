package it.unicam.cs.mdp.vectorrace.model.game.validators;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Defines the contract for movement validation in the Vector Race game.
 * This interface is part of a validation chain that ensures all game rules
 * are followed during player movements.
 * 
 * <p>Implementations of this interface can validate different aspects of movement:
 * <ul>
 *   <li>Wall collisions</li>
 *   <li>Player collisions</li>
 *   <li>Track boundaries</li>
 *   <li>Other game-specific rules</li>
 * </ul>
 * 
 * <p>The validation chain follows the Chain of Responsibility pattern,
 * where each validator can independently approve or reject a movement.
 */
public interface MoveValidator {
    
    /**
     * Validates a movement from a starting position to an ending position.
     * This method checks if the proposed movement adheres to the specific
     * rules implemented by this validator.
     *
     * @param start The starting position of the movement.
     * @param end The intended ending position of the movement.
     * @param player The player attempting the movement (may be null for temporary states).
     * @param gameState The current state of the game containing track and player information.
     * @return true if the movement is valid according to this validator's rules,
     *         false if the movement violates any rules.
     */
    boolean isValidMove(Position start, Position end, Player player, GameState gameState);
}