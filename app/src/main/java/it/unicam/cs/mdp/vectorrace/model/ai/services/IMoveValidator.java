package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Defines the interface for validating player movements in the Vector Race game.
 * Implementations of this interface encapsulate the logic for determining
 * whether a proposed move is valid according to the game rules and track constraints.
 *
 * <p>This interface supports two types of move validation:
 * <ul>
 *   <li>Temporary move validation - Used during pathfinding to quickly assess
 *       the feasibility of a move without considering all game rules</li>
 *   <li>Real move validation - Used to validate a player's actual move,
 *       taking into account all game rules and constraints</li>
 * </ul>
 */
public interface IMoveValidator {
    /**
     * Validates a temporary move during pathfinding.
     * This method is used for quick validation of potential moves, typically
     * only checking for basic constraints like wall collisions.
     *
     * @param start The starting position.
     * @param velocity The velocity vector.
     * @param track The game track.
     * @return true if the move is valid for pathfinding, false otherwise.
     */
    boolean validateTempMove(Position start, Vector velocity, Track track);

    /**
     * Validates a real move made by a player.
     * This method performs a complete validation of the move, considering
     * all game rules, player attributes, and the current game state.
     *
     * @param player The player making the move.
     * @param acceleration The acceleration vector.
     * @param gameState The current game state.
     * @return true if the move is valid, false otherwise.
     */
    boolean validateRealMove(Player player, Vector acceleration, GameState gameState);
}