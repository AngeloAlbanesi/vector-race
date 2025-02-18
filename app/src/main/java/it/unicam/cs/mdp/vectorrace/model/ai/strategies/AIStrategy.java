package it.unicam.cs.mdp.vectorrace.model.ai.strategies;

import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Defines the interface for AI strategies in the Vector Race game.
 * Implementations of this interface provide different algorithms for
 * determining the next acceleration vector for bot players.
 *
 * <p>AI strategies are responsible for:
 * <ul>
 *   <li>Analyzing the current game state</li>
 *   <li>Evaluating potential moves</li>
 *   <li>Selecting the optimal acceleration vector</li>
 * </ul>
 */
public interface AIStrategy {

    /**
     * Calculates the next acceleration vector for the player.
     * This method is the core of the AI strategy, determining how the bot
     * will move based on the current game conditions.
     *
     * @param player The player for whom to calculate the move.
     * @param gameState The current state of the game.
     * @return The acceleration vector chosen by the AI strategy.
     */
    Vector getNextAcceleration(Player player, GameState gameState);
}