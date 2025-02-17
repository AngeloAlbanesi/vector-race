package it.unicam.cs.mdp.vectorrace.model.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test unitari per la classe Position.
 */
public class PositionTest {

    private Position position;
    private final int x = 5;
    private final int y = 10;

    @BeforeEach
    void setUp() {
        position = new Position(x, y);
    }

    @Test
    void testCostruzioneEGetters() {
        assertEquals(x, position.getX(), "La coordinata X deve corrispondere al valore fornito al costruttore");
        assertEquals(y, position.getY(), "La coordinata Y deve corrispondere al valore fornito al costruttore");
    }

    @Test
    void testDistanceTo() {
        Position other = new Position(8, 14);
        // La distanza calcolata dovrebbe essere sqrt((8-5)^2 + (14-10)^2) = 5
        assertEquals(5.0, position.distanceTo(other), 0.001, 
            "La distanza euclidea tra (5,10) e (8,14) deve essere 5");
        
        // Test distanza con stessa posizione
        assertEquals(0.0, position.distanceTo(position), 0.001,
            "La distanza tra una posizione e se stessa deve essere 0");
    }

    @Test
    void testAdd() {
        Vector vector = new Vector(3, -2);
        Position newPosition = position.add(vector);
        
        assertEquals(8, newPosition.getX(), "L'addizione sulla X deve essere corretta");
        assertEquals(8, newPosition.getY(), "L'addizione sulla Y deve essere corretta");
        
        // Verifica che la posizione originale non sia stata modificata
        assertEquals(x, position.getX(), "La posizione originale non deve essere modificata");
        assertEquals(y, position.getY(), "La posizione originale non deve essere modificata");
    }

    @Test
    void testMove() {
        Vector vector = new Vector(3, -2);
        Position newPosition = position.move(vector);
        
        // move dovrebbe comportarsi come add
        assertEquals(position.add(vector), newPosition, 
            "Il metodo move deve comportarsi come il metodo add");
    }

    @Test
    void testEquals() {
        Position samePosition = new Position(x, y);
        Position differentPosition = new Position(x + 1, y);

        assertTrue(position.equals(position), "Una posizione deve essere uguale a se stessa");
        assertTrue(position.equals(samePosition), "Posizioni con stesse coordinate devono essere uguali");
        assertFalse(position.equals(differentPosition), "Posizioni con coordinate diverse non devono essere uguali");
        assertFalse(position.equals(null), "Una posizione non deve essere uguale a null");
        assertFalse(position.equals(new Object()), "Una posizione non deve essere uguale a un oggetto di tipo diverso");
    }

    @Test
    void testHashCode() {
        Position samePosition = new Position(x, y);
        assertEquals(position.hashCode(), samePosition.hashCode(),
            "Posizioni uguali devono avere lo stesso hashcode");
    }

    @Test
    void testToString() {
        assertEquals("(" + x + ", " + y + ")", position.toString(),
            "Il formato della stringa deve essere (x, y)");
    }
}