package it.unicam.cs.mdp.vectorrace.model.game.validators;

import java.util.HashSet;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Validatore specifico per le collisioni con altri giocatori.
 */
public class PlayerCollisionValidator implements MoveValidator {
    
    @Override
    public boolean isValidMove(Position start, Position end, Player player, GameState gameState) {
        // Se è uno stato temporaneo o non c'è un player, non controlliamo le collisioni
        if (gameState == null || player == null || gameState.isTemporary()) {
            return true;
        }

        // Se la velocità è zero, controlliamo solo la destinazione
        if (player.getVelocity().isZero()) {
            return !isCellOccupiedByStationaryPlayer(end, gameState, player);
        }

        // Altrimenti controlliamo tutto il percorso
        Set<Position> occupiedCells = getOccupiedPositions(gameState, player);
        
        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int steps = calculateSteps(dx, dy);
        
        if (steps == 0) {
            return true;
        }
        
        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;
        
        return checkPathForPlayers(start, xIncrement, yIncrement, steps, occupiedCells);
    }
    
    /**
     * Calcola il numero di passi necessari per il movimento
     */
    private int calculateSteps(int dx, int dy) {
        return Math.max(Math.abs(dx), Math.abs(dy));
    }
    
    /**
     * Controlla se ci sono giocatori fermi lungo il percorso
     */
    private boolean checkPathForPlayers(Position start, float xIncrement, float yIncrement, 
            int steps, Set<Position> occupiedCells) {
        float x = start.getX();
        float y = start.getY();
        
        for (int i = 0; i <= steps; i++) {
            int currentX = Math.round(x);
            int currentY = Math.round(y);
            
            // Salta la cella di partenza
            if (!(currentX == start.getX() && currentY == start.getY())) {
                if (occupiedCells.contains(new Position(currentX, currentY))) {
                    return false;
                }
            }
            
            x += xIncrement;
            y += yIncrement;
        }
        
        return true;
    }
    
    /**
     * Ottiene l'insieme delle posizioni occupate da giocatori fermi
     */
    private Set<Position> getOccupiedPositions(GameState gameState, Player currentPlayer) {
        Set<Position> occupied = new HashSet<>();
        for (Player other : gameState.getPlayers()) {
            if (!other.getName().equals(currentPlayer.getName()) && other.getVelocity().isZero()) {
                occupied.add(other.getPosition());
            }
        }
        return occupied;
    }
    
    /**
     * Verifica se una cella è occupata da un giocatore fermo
     */
    private boolean isCellOccupiedByStationaryPlayer(Position pos, GameState gameState, Player currentPlayer) {
        for (Player other : gameState.getPlayers()) {
            if (!other.getName().equals(currentPlayer.getName()) && 
                other.getVelocity().isZero() && 
                other.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }
}