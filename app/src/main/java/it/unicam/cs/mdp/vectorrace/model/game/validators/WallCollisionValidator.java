package it.unicam.cs.mdp.vectorrace.model.game.validators;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Validatore specifico per le collisioni con i muri.
 */
public class WallCollisionValidator implements MoveValidator {
    
    @Override
    public boolean isValidMove(Position start, Position end, Player player, GameState gameState) {
        Track track = gameState != null ? gameState.getTrack() : null;
        if (track == null) {
            throw new IllegalArgumentException("Track non può essere null");
        }
        
        // Se la posizione è la stessa, il movimento è valido
        if (start.equals(end)) {
            return true;
        }
        
        return isPathClear(start, end, track);
    }
    
    /**
     * Verifica che il percorso sia libero da muri usando l'algoritmo di Bresenham.
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
     * Calcola il numero di passi necessari per il movimento.
     */
    private int calculateSteps(int dx, int dy) {
        return Math.max(Math.abs(dx), Math.abs(dy));
    }
    
    /**
     * Controlla ogni cella del percorso per verificare la presenza di muri.
     */
    private boolean checkPath(Position start, float xIncrement, float yIncrement, int steps, Track track) {
        float x = start.getX();
        float y = start.getY();
        
        for (int i = 0; i <= steps; i++) {
            int currentX = Math.round(x);
            int currentY = Math.round(y);
            
            // Salta la cella di partenza
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