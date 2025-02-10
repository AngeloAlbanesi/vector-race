package it.unicam.cs.mdp.vectorrace.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Gestisce i controlli di validita' dei movimenti sul circuito (muri e collisioni).
 */
public class MovementManager {

    /**
     * Verifica se un giocatore puo' effettuare la mossa (accelerazione),
     * controllando muri e giocatori fermi.
     */
    public boolean validateMove(Player player, Vector acceleration, GameState gameState) {
        Vector newVelocity = player.getVelocity().add(acceleration);
        Position newPosition = player.getPosition().move(newVelocity);
        return isPathClear(player.getPosition(), newPosition, gameState.getTrack(), player, gameState);
    }

    /**
     * Controlla se il percorso e' libero da muri e (opzionalmente) da giocatori fermi.
     */
    public boolean isPathClear(Position start, Position end, Track track, Player player, GameState gameState) {
        if (player.getVelocity().isZero()) {
            // Se la velocita' era zero, controlliamo solo la destinazione
            return track.getCell(end.getX(), end.getY()) != CellType.WALL
                    && !isCellOccupiedByStationaryPlayer(end, gameState, player);
        }

        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return true;

        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;

        Set<Position> occupiedCells = getOccupiedPositions(gameState, player);

        float x = start.getX();
        float y = start.getY();

        for (int i = 0; i <= steps; i++) {
            int currentX = Math.round(x);
            int currentY = Math.round(y);

            // salta la cella di partenza
            if (!(currentX == start.getX() && currentY == start.getY())) {
                if (track.getCell(currentX, currentY) == CellType.WALL) {
                    return false;
                }
                // Se questa cella e' occupata da un giocatore fermo, no
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
     * Versione "stateless" usata dal PathFinder (A*), che non controlla collisioni con altri giocatori in movimento,
     * ma solo i muri.
     */
    public boolean validateMoveTemp(Position start, Vector velocity, Track track) {
        Position end = start.add(velocity);

        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {
            return true; 
        }

        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;
        float x = start.getX();
        float y = start.getY();

        for (int i = 0; i <= steps; i++) {
            int currentX = Math.round(x);
            int currentY = Math.round(y);

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

    /**
     * Restituisce le posizioni dei giocatori "fermi" (velocity zero),
     * da considerare come occupate.
     */
    private Set<Position> getOccupiedPositions(GameState gameState, Player currentPlayer) {
        Set<Position> occupied = new HashSet<>();
        for (Player other : gameState.getPlayers()) {
            if (!other.getName().equals(currentPlayer.getName())) {
                if (other.getVelocity().isZero()) {
                    occupied.add(other.getPosition());
                }
            }
        }
        return occupied;
    }

    /**
     * Verifica se la cella 'pos' e' occupata da un giocatore fermo (diverso da 'currentPlayer').
     */
    private boolean isCellOccupiedByStationaryPlayer(Position pos, GameState gameState, Player currentPlayer) {
        for (Player other : gameState.getPlayers()) {
            if (!other.getName().equals(currentPlayer.getName())) {
                if (other.getVelocity().isZero() && other.getPosition().equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }
}
