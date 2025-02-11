package it.unicam.cs.mdp.vectorrace.model.ai.state;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Mantiene lo stato e le metriche del percorso per un bot.
 */
public class PathState {
    private final Position currentPosition;
    private final Vector currentVelocity;
    private final Position targetPosition;
    private final double alignmentScore;
    private final double distanceScore;
    private final double overtakingScore;

    public PathState(Position currentPosition, Vector currentVelocity,
            Position targetPosition, double alignmentScore,
            double distanceScore, double overtakingScore) {
        this.currentPosition = currentPosition;
        this.currentVelocity = currentVelocity;
        this.targetPosition = targetPosition;
        this.alignmentScore = alignmentScore;
        this.distanceScore = distanceScore;
        this.overtakingScore = overtakingScore;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public Vector getCurrentVelocity() {
        return currentVelocity;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }

    public double getAlignmentScore() {
        return alignmentScore;
    }

    public double getDistanceScore() {
        return distanceScore;
    }

    public double getOvertakingScore() {
        return overtakingScore;
    }

    /**
     * Calcola lo score totale del percorso.
     */
    public double getTotalScore() {
        // Pesi per i diversi fattori
        double alignmentWeight = 0.4;
        double distanceWeight = 0.4;
        double overtakingWeight = 0.2;

        return (alignmentScore * alignmentWeight) +
                (distanceScore * distanceWeight) +
                (overtakingScore * overtakingWeight);
    }
}