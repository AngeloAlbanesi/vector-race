package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bresenham;

import java.util.ArrayList;
import java.util.List;
import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Classe che implementa l'algoritmo di Bresenham per il calcolo del percorso
 * tra due punti.
 * Questa implementazione è ottimizzata per il tracciamento dei checkpoint nel
 * gioco.
 */
public class BresenhamPathCalculator {

    /**
     * Calcola tutti i punti del percorso tra due posizioni usando l'algoritmo di
     * Bresenham.
     * 
     * @param start posizione di partenza
     * @param end   posizione di arrivo
     * @return lista di tutte le posizioni nel percorso, inclusi start e end
     */
    public List<Position> calculatePath(Position start, Position end) {
        List<Position> path = new ArrayList<>();

        int x0 = start.getX();
        int y0 = start.getY();
        int x1 = end.getX();
        int y1 = end.getY();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int signX = (x1 > x0) ? 1 : (x1 < x0 ? -1 : 0);
        int signY = (y1 > y0) ? 1 : (y1 < y0 ? -1 : 0);

        // Using a supercover Bresenham algorithm to include all cells intersected by
        // the line.
        int ix = 0, iy = 0;
        path.add(new Position(x0, y0));

        // Loop until we have stepped through all differences
        while (ix < dx || iy < dy) {
            double tx = (dx == 0) ? Double.MAX_VALUE : ((ix + 0.5) / dx);
            double ty = (dy == 0) ? Double.MAX_VALUE : ((iy + 0.5) / dy);

            if (tx < ty) {
                ix++;
                x0 += signX;
            } else if (ty < tx) {
                iy++;
                y0 += signY;
            } else { // tx == ty; move diagonally
                ix++;
                iy++;
                x0 += signX;
                y0 += signY;
            }
            path.add(new Position(x0, y0));
        }

        return path;
    }
}