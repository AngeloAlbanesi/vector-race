package it.unicam.cs.mdp.vectorrace.model.game.validators;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * A specialized validator that checks for collisions between player movements and walls.
 * This validator implements a modified Bresenham's line algorithm to ensure that
 * player movements don't pass through any wall cells on the track.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Continuous path checking using line-drawing algorithm</li>
 *   <li>Efficient wall collision detection</li>
 *   <li>Handles edge cases like zero-length movements</li>
 * </ul>
 * 
 * <p>The validation process:
 * <ol>
 *   <li>Validates input parameters and handles edge cases</li>
 *   <li>Calculates movement path using interpolation</li>
 *   <li>Checks each cell along the path for walls</li>
 * </ol>
 */
public class WallCollisionValidator implements MoveValidator {
    
    @Override
    public boolean isValidMove(Position start, Position end, Player player, GameState gameState) {
        Track track = gameState != null ? gameState.getTrack() : null;
        if (track == null) {
            throw new IllegalArgumentException("Track non può essere null");
        }
        
        // Optimization: Same position is always valid
        if (start.equals(end)) {
            return true;
        }
        
        return isPathClear(start, end, track);
    }
    
    /**
     * Checks if the path between two positions is clear of walls.
     * Uses a modified Bresenham's line algorithm to determine all cells
     * that the movement would pass through.
     *
     * @param start Starting position of the movement.
     * @param end Ending position of the movement.
     * @param track The track containing wall information.
     * @return true if the path is clear of walls, false otherwise.
     */
    private boolean isPathClear(Position start, Position end, Track track) {
        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int steps = calculateSteps(dx, dy);
        
        if (steps == 0) {
            return true;
        }
        
        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;
        
        return checkPath(start, xIncrement, yIncrement, steps, track);
    }
    
    /**
     * Calculates the number of steps needed for the movement.
     * Uses the maximum distance in either direction to ensure all
     * intermediate cells are checked.
     *
     * @param dx The change in x-coordinate.
     * @param dy The change in y-coordinate.
     * @return The number of steps needed for the movement.
     */
    private int calculateSteps(int dx, int dy) {
        return Math.max(Math.abs(dx), Math.abs(dy));
    }
    
    /**
     * Checks each cell along the movement path for walls.
     * This method:
     * <ul>
     *   <li>Interpolates positions along the movement path</li>
     *   <li>Rounds floating-point coordinates to grid positions</li>
     *   <li>Checks each position for wall cells</li>
     *   <li>Skips the starting position in the check</li>
     * </ul>
     *
     * @param start The starting position.
     * @param xIncrement The x-coordinate increment per step.
     * @param yIncrement The y-coordinate increment per step.
     * @param steps The total number of steps to check.
     * @param track The track containing wall information.
     * @return true if no walls are encountered, false otherwise.
     */
    private boolean checkPath(Position start, float xIncrement, float yIncrement, int steps, Track track) {
        float x = start.getX();
        float y = start.getY();
        
        for (int i = 0; i <= steps; i++) {
            int currentX = Math.round(x);
            int currentY = Math.round(y);
            
            // Skip starting position to allow movement from adjacent to walls
            if (!(currentX == start.getX() && currentY == start.getY())) {
                if (track.getCell(currentX, currentY) == CellType.WALL) {
                    return false;
                }
            }
            
            x += xIncrement;
            y += yIncrement;
        }
        
        return true;
    }
}