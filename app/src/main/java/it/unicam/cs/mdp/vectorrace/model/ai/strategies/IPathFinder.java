package it.unicam.cs.mdp.vectorrace.model.ai.strategies;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Defines the contract for pathfinding implementations used by AI strategies.
 * Implementations of this interface are responsible for finding the next
 * acceleration vector to reach a target position, considering the current
 * game state and player attributes.
 *
 * <p>Pathfinding algorithms should:
 * <ul>
 *   <li>Analyze the current game state</li>
 *   <li>Consider player's position and velocity</li>
 *   <li>Calculate a valid acceleration vector to move towards the target</li>
 * </ul>
 */
public interface IPathFinder {
    /**
     * Finds the next acceleration vector to reach the target position.
     *
     * @param player The current player.
     * @param gameState The current game state.
     * @param target The target position to reach.
     * @return The acceleration vector to apply.
     */
    Vector findPath(Player player, GameState gameState, Position target);
}