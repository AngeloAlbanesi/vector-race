package it.unicam.cs.mdp.vectorrace.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe astratta che rappresenta un giocatore (umano o bot).
 */
public abstract class Player {
    protected final String name;
    protected final Color color;
    protected Position position;
    protected Vector velocity;
    protected final List<Position> movementHistory;
    protected int nextCheckpointIndex = 1;

    public Player(String name, Color color, Position startPosition) {
        this.name = name;
        this.color = color;
        this.position = startPosition;
        this.velocity = new Vector(0, 0);
        this.movementHistory = new ArrayList<>();
        this.movementHistory.add(startPosition);
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public int getNextCheckpointIndex() {
        return nextCheckpointIndex;
    }

    public void incrementCheckpointIndex() {
        nextCheckpointIndex++;
    }

    public abstract Vector getNextAcceleration(GameState gameState);

    public void updatePosition(Position newPosition) {
        this.position = newPosition;
        this.movementHistory.add(newPosition);
    }

    public void updateVelocity(Vector newVelocity) {
        this.velocity = newVelocity;
    }

    public void resetVelocity() {
        this.velocity = new Vector(0, 0);
    }

    @Override
    public String toString() {
        return name + " in " + position + " con velocità " + velocity;
    }
}