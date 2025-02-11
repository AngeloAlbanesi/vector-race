package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Implementazione della funzione euristica che usa la distanza di Chebyshev.
 * Questa distanza è calcolata come il massimo tra le differenze assolute delle coordinate.
 * Con costo di 1 per ogni "mossa" (diagonale o no), è ammissibile per A*.
 */
public class ChebyshevHeuristic implements IHeuristicCalculator {

    @Override
    public double calculate(Position current, Position target) {
        if (current == null || target == null) {
            throw new IllegalArgumentException("Le posizioni non possono essere null");
        }
        
        int dx = Math.abs(current.getX() - target.getX());
        int dy = Math.abs(current.getY() - target.getY());
        return Math.max(dx, dy);
    }
}