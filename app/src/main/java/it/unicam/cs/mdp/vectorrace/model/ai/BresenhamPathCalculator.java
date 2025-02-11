package it.unicam.cs.mdp.vectorrace.model.ai;

import java.util.ArrayList;
import java.util.List;
import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Classe che implementa l'algoritmo di Bresenham per il calcolo del percorso tra due punti.
 * Questa implementazione è ottimizzata per il tracciamento dei checkpoint nel gioco.
 */
public class BresenhamPathCalculator {
    
    /**
     * Calcola tutti i punti del percorso tra due posizioni usando l'algoritmo di Bresenham.
     * 
     * @param start posizione di partenza
     * @param end posizione di arrivo
     * @return lista di tutte le posizioni nel percorso, inclusi start e end
     */
    public List<Position> calculatePath(Position start, Position end) {
        List<Position> path = new ArrayList<>();
        
        int x1 = start.getX(), y1 = start.getY();
        int x2 = end.getX(), y2 = end.getY();
        
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        
        int x = x1, y = y1;
        
        while (true) {
            path.add(new Position(x, y));
            
            if (x == x2 && y == y2) {
                break;
            }
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        
        return path;
    }
}