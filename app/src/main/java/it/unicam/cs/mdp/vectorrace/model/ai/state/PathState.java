package it.unicam.cs.mdp.vectorrace.model.ai.state;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

/**
 * Maintains the state and metrics of a path for a bot player.
 * This class encapsulates various factors that contribute to the
 * evaluation of a potential path, such as alignment, distance, and overtaking opportunities.
 *
 * <p>Key attributes:
 * <ul>
 *   <li>currentPosition - The current position of the bot</li>
 *   <li>currentVelocity - The current velocity of the bot</li>
 *   <li>targetPosition - The target position for the path</li>
 *   <li>alignmentScore - A score representing how well the path aligns with the target</li>
 *   <li>distanceScore - A score representing the distance to the target</li>
 *   <li>overtakingScore - A score representing potential overtaking opportunities</li>
 * </ul>
 */
public class PathState {
    private final Position currentPosition;
    private final Vector currentVelocity;
    private final Position targetPosition;
    private final double alignmentScore;
    private final double distanceScore;
    private final double overtakingScore;

    /**
     * Creates a new PathState instance.
     *
     * @param currentPosition The current position of the bot.
     * @param currentVelocity The current velocity of the bot.
     * @param targetPosition The target position for the path.
     * @param alignmentScore A score representing how well the path aligns with the target.
     * @param distanceScore A score representing the distance to the target.
     * @param overtakingScore A score representing potential overtaking opportunities.
     */
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

    /**
     * Gets the current position of the bot.
     *
     * @return The current position.
     */
    public Position getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Gets the current velocity of the bot.
     *
     * @return The current velocity.
     */
    public Vector getCurrentVelocity() {
        return currentVelocity;
    }

    /**
     * Gets the target position for the path.
     *
     * @return The target position.
     */
    public Position getTargetPosition() {
        return targetPosition;
    }

    /**
     * Gets the alignment score.
     *
     * @return The alignment score.
     */
    public double getAlignmentScore() {
        return alignmentScore;
    }

    /**
     * Gets the distance score.
     *
     * @return The distance score.
     */
    public double getDistanceScore() {
        return distanceScore;
    }

    /**
     * Gets the overtaking score.
     *
     * @return The overtaking score.
     */
    public double getOvertakingScore() {
        return overtakingScore;
    }

    /**
     * Calculates the total score of the path.
     * This method combines the alignment, distance, and overtaking scores
     * using predefined weights to determine the overall path quality.
     *
     * @return The total score of the path.
     */
    public double getTotalScore() {
        // Weights for the different factors
        double alignmentWeight = 0.4;
        double distanceWeight = 0.4;
        double overtakingWeight = 0.2;

        return (alignmentScore * alignmentWeight) +
                (distanceScore * distanceWeight) +
                (overtakingScore * overtakingWeight);
    }
}