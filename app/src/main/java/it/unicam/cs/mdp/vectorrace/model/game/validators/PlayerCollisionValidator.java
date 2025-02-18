package it.unicam.cs.mdp.vectorrace.model.game.validators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bresenham.BresenhamPathCalculator;

/**
 * Validatore specifico per le collisioni con altri giocatori.
 */
public class PlayerCollisionValidator implements MoveValidator {
    
    private final BresenhamPathCalculator pathCalculator;
    
    public PlayerCollisionValidator() {
        this.pathCalculator = new BresenhamPathCalculator();
    }
    
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
        Set<Position> occupiedPositions = getOccupiedPositions(gameState, player);
        List<Position> path = pathCalculator.calculatePath(start, end);
        
        // Skip the starting position in the check
        for (int i = 1; i < path.size(); i++) {
            if (occupiedPositions.contains(path.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Ottiene l'insieme delle posizioni occupate da giocatori fermi
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
     * Verifica se una cella è occupata da un giocatore fermo
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