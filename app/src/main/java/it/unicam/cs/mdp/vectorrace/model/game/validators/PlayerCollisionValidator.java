package it.unicam.cs.mdp.vectorrace.model.game.validators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bresenham.BresenhamPathCalculator;

/**
 * A specialized validator that checks for collisions between players during movement.
 * This validator ensures that players cannot move through or end their movement on 
 * positions occupied by other players.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Uses Bresenham's line algorithm for path calculation</li>
 *   <li>Handles both moving and stationary collisions</li>
 *   <li>Ignores validation for temporary game states (used in pathfinding)</li>
 * </ul>
 * 
 * <p>The validation process varies based on the player's state:
 * <ul>
 *   <li>For stationary players: only checks the destination position</li>
 *   <li>For moving players: checks the entire movement path</li>
 * </ul>
 */
public class PlayerCollisionValidator implements MoveValidator {
    
    private final BresenhamPathCalculator pathCalculator;
    
    /**
     * Creates a new PlayerCollisionValidator.
     * Initializes the Bresenham path calculator used for continuous collision detection.
     */
    public PlayerCollisionValidator() {
        this.pathCalculator = new BresenhamPathCalculator();
    }
    
    @Override
    public boolean isValidMove(Position start, Position end, Player player, GameState gameState) {
        // Skip collision checks for temporary states or null players
        if (gameState == null || player == null || gameState.isTemporary()) {
            return true;
        }

        // For stationary players, only check the destination
        if (player.getVelocity().isZero()) {
            return !isCellOccupiedByStationaryPlayer(end, gameState, player);
        }

        // For moving players, check the entire path
        Set<Position> occupiedPositions = getOccupiedPositions(gameState, player);
        List<Position> path = pathCalculator.calculatePath(start, end);
        
        // Skip the starting position in the check (index 0)
        for (int i = 1; i < path.size(); i++) {
            if (occupiedPositions.contains(path.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets all positions currently occupied by stationary players.
     * This method collects the positions of all players except the current one
     * to create a set of positions that should be avoided during movement.
     *
     * @param gameState The current game state.
     * @param currentPlayer The player attempting to move.
     * @return A set of positions occupied by other players.
     */
    private Set<Position> getOccupiedPositions(GameState gameState, Player currentPlayer) {
        Set<Position> occupied = new HashSet<>();
        for (Player other : gameState.getPlayers()) {
            if (!other.getName().equals(currentPlayer.getName())) {
                occupied.add(other.getPosition());
            }
        }
        return occupied;
    }
    
    /**
     * Checks if a specific position is occupied by a stationary player.
     * This method is used when the moving player has zero velocity and
     * only needs to check the destination position.
     *
     * @param pos The position to check.
     * @param gameState The current game state.
     * @param currentPlayer The player attempting to move.
     * @return true if the position is occupied by another player, false otherwise.
     */
    private boolean isCellOccupiedByStationaryPlayer(Position pos, GameState gameState, Player currentPlayer) {
        for (Player other : gameState.getPlayers()) {
            if (!other.getName().equals(currentPlayer.getName()) && 
                other.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }
}