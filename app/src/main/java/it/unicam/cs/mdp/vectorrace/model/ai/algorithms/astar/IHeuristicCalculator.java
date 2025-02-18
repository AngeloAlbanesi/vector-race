package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Defines the interface for heuristic calculation in the A* search algorithm.
 * Implementations of this interface provide a method to estimate the cost
 * from a given position to the target position.
 *
 * <p>Heuristic functions are used to guide the A* search, allowing it to
 * efficiently explore the search space and find optimal paths.
 *
 * <p>Implementations should adhere to the following principles:
 * <ul>
 *   <li>Admissibility: never overestimate the actual cost</li>
 *   <li>Consistency: the heuristic estimate should be consistent</li>
 * </ul>
 */
public interface IHeuristicCalculator {
    /**
     * Calculates the heuristic value between two positions.
     *
     * @param current The current position.
     * @param target The target position.
     * @return The estimated heuristic value.
     */
    double calculate(Position current, Position target);
}