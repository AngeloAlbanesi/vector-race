package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Implements a heuristic function using Chebyshev distance.
 * This heuristic estimates the cost from a given position to the target
 * position in the Vector Race game.
 *
 * <p>Chebyshev distance is calculated as the maximum of the absolute
 * differences between the coordinates:
 * <pre>
 *   h(x, y) = max(|x1 - x2|, |y1 - y2|)
 * </pre>
 *
 * <p>With a cost of 1 for each move (diagonal or straight), this heuristic
 * is admissible for the A* search algorithm, meaning it never overestimates
 * the actual cost to reach the goal.
 */
public class ChebyshevHeuristic implements IHeuristicCalculator {

    @Override
    public double calculate(Position current, Position target) {
        if (current == null || target == null) {
            throw new IllegalArgumentException("Positions cannot be null");
        }
        
        int dx = Math.abs(current.getX() - target.getX());
        int dy = Math.abs(current.getY() - target.getY());
        return Math.max(dx, dy);
    }
}