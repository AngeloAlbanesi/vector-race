package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bresenham;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test per la classe BresenhamPathCalculator.
 * Verifica il corretto calcolo del percorso tra due punti usando l'algoritmo di Bresenham.
 */
public class BresenhamPathCalculatorTest {
    private BresenhamPathCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new BresenhamPathCalculator();
    }

    @Test
    void testHorizontalPath() {
        Position start = new Position(0, 0);
        Position end = new Position(3, 0);
        
        List<Position> path = calculator.calculatePath(start, end);
        
        // Verifica che il percorso contenga tutti i punti orizzontali
        assertEquals(4, path.size()); // start, 1, 2, end
        assertTrue(path.contains(new Position(0, 0)));
        assertTrue(path.contains(new Position(1, 0)));
        assertTrue(path.contains(new Position(2, 0)));
        assertTrue(path.contains(new Position(3, 0)));
    }

    @Test
    void testVerticalPath() {
        Position start = new Position(0, 0);
        Position end = new Position(0, 3);
        
        List<Position> path = calculator.calculatePath(start, end);
        
        // Verifica che il percorso contenga tutti i punti verticali
        assertEquals(4, path.size());
        assertTrue(path.contains(new Position(0, 0)));
        assertTrue(path.contains(new Position(0, 1)));
        assertTrue(path.contains(new Position(0, 2)));
        assertTrue(path.contains(new Position(0, 3)));
    }

    @Test
    void testDiagonalPath() {
        Position start = new Position(0, 0);
        Position end = new Position(2, 2);
        
        List<Position> path = calculator.calculatePath(start, end);
        
        // Verifica che il percorso contenga tutti i punti della diagonale
        assertEquals(3, path.size());
        assertTrue(path.contains(new Position(0, 0)));
        assertTrue(path.contains(new Position(1, 1)));
        assertTrue(path.contains(new Position(2, 2)));
    }

    @Test
    void testGenericPath() {
        Position start = new Position(0, 0);
        Position end = new Position(2, 3);
        
        List<Position> path = calculator.calculatePath(start, end);
        
        // Verifica che il percorso contenga start e end
        assertTrue(path.contains(start));
        assertTrue(path.contains(end));
        
        // Verifica che tutti i punti siano adiacenti
        for (int i = 1; i < path.size(); i++) {
            Position prev = path.get(i - 1);
            Position curr = path.get(i);
            int dx = Math.abs(curr.getX() - prev.getX());
            int dy = Math.abs(curr.getY() - prev.getY());
            // Ogni punto deve essere adiacente al precedente (spostamento di max 1 unità)
            assertTrue(dx <= 1 && dy <= 1);
        }
    }

    @Test
    void testSinglePointPath() {
        Position point = new Position(1, 1);
        
        List<Position> path = calculator.calculatePath(point, point);
        
        // Il percorso deve contenere solo il punto stesso
        assertEquals(1, path.size());
        assertEquals(point, path.get(0));
    }

    @Test
    void testNegativeCoordinates() {
        Position start = new Position(-1, -1);
        Position end = new Position(1, 1);
        
        List<Position> path = calculator.calculatePath(start, end);
        
        // Verifica che il percorso gestisca correttamente le coordinate negative
        assertTrue(path.contains(start));
        assertTrue(path.contains(end));
        assertEquals(3, path.size());
    }
}