package it.unicam.cs.mdp.vectorrace.model;

import java.util.HashSet;
import java.util.Set;

public class MovementManager {

    public boolean validateMove(Player player, Vector acceleration, GameState gameState) {
        Vector newVelocity = player.getVelocity().add(acceleration);
        Position newPosition = player.getPosition().move(newVelocity);
        return isPathClear(player.getPosition(), newPosition, gameState.getTrack(), player, gameState);
    }

    public boolean isPathClear(Position start, Position end, Track track, Player player, GameState gameState) {
        // Per mosse iniziali (velocità 0), controlla solo la destinazione
        if (player.getVelocity().isZero()) {
            return track.getCell(end.getX(), end.getY()) != CellType.WALL;
        }

        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0)
            return true;

        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;

        // Crea set delle celle occupate (esclusa posizione di partenza)
        Set<Position> occupiedCells = getOccupiedPositions(gameState, player);

        // Controlla il percorso per collisioni
        float x = start.getX();
        float y = start.getY();

        for (int i = 0; i <= steps; i++) {
            int currentX = Math.round(x);
            int currentY = Math.round(y);

            // Salta la posizione iniziale
            if (!(currentX == start.getX() && currentY == start.getY())) {
                if (track.getCell(currentX, currentY) == CellType.WALL) {
                    return false;
                }
                if (occupiedCells.contains(new Position(currentX, currentY))) {
                    return false;
                }
            }
            x += xIncrement;
            y += yIncrement;
        }

        return true;
    }

    private Set<Position> getOccupiedPositions(GameState gameState, Player currentPlayer) {
        Set<Position> occupied = new HashSet<>();
        for (Player other : gameState.getPlayers()) {
            if (!other.getName().equals(currentPlayer.getName())) {
                Vector otherVel = other.getVelocity();
                // Considera solo i giocatori fermi come ostacoli
                if (otherVel.isZero()) {
                    occupied.add(other.getPosition());
                }
            }
        }
        return occupied;
    }
}