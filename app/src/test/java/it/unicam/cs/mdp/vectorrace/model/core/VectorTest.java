package it.unicam.cs.mdp.vectorrace.model.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test unitari per la classe Vector.
 */
public class VectorTest {

    private Vector vector;
    private final int dx = 3;
    private final int dy = -2;

    @BeforeEach
    void setUp() {
        vector = new Vector(dx, dy);
    }

    @Test
    void testCostruzioneEGetters() {
        assertEquals(dx, vector.getDx(), "La componente X deve corrispondere al valore fornito al costruttore");
        assertEquals(dy, vector.getDy(), "La componente Y deve corrispondere al valore fornito al costruttore");
    }

    @Test
    void testVettoreZero() {
        assertEquals(0, Vector.ZERO.getDx(), "Il vettore ZERO deve avere componente X = 0");
        assertEquals(0, Vector.ZERO.getDy(), "Il vettore ZERO deve avere componente Y = 0");
        assertTrue(Vector.ZERO.isZero(), "Il vettore ZERO deve essere riconosciuto come zero");
    }

    @Test
    void testAdd() {
        Vector other = new Vector(2, 5);
        Vector sum = vector.add(other);
        
        assertEquals(dx + 2, sum.getDx(), "La somma delle componenti X deve essere corretta");
        assertEquals(dy + 5, sum.getDy(), "La somma delle componenti Y deve essere corretta");
        
        // Test addizione con vettore zero
        assertEquals(vector, vector.add(Vector.ZERO), 
            "L'addizione con il vettore zero non deve modificare il vettore");
    }

    @Test
    void testSubtract() {
        Vector other = new Vector(1, 1);
        Vector diff = vector.subtract(other);
        
        assertEquals(dx - 1, diff.getDx(), "La differenza delle componenti X deve essere corretta");
        assertEquals(dy - 1, diff.getDy(), "La differenza delle componenti Y deve essere corretta");
        
        // Test sottrazione con vettore zero
        assertEquals(vector, vector.subtract(Vector.ZERO), 
            "La sottrazione del vettore zero non deve modificare il vettore");
        
        // Test sottrazione di un vettore da se stesso
        Vector zeroResult = vector.subtract(vector);
        assertTrue(zeroResult.isZero(), 
            "La sottrazione di un vettore da se stesso deve dare il vettore zero");
    }

    @Test
    void testIsZero() {
        assertFalse(vector.isZero(), "Un vettore non nullo non deve essere zero");
        assertTrue(new Vector(0, 0).isZero(), "Un vettore (0,0) deve essere zero");
        assertTrue(Vector.ZERO.isZero(), "Il vettore ZERO deve essere zero");
    }

    @Test
    void testGetMaxSpeed() {
        // Test con vettore del setup
        assertEquals(Math.max(Math.abs(dx), Math.abs(dy)), vector.getMaxSpeed(),
            "La velocità massima deve essere il massimo tra i valori assoluti delle componenti");
        
        // Test con vettore con componenti uguali
        Vector equalComponents = new Vector(3, 3);
        assertEquals(3, equalComponents.getMaxSpeed(),
            "Con componenti uguali, la velocità massima deve essere il valore assoluto di una componente");
        
        // Test con vettore zero
        assertEquals(0, Vector.ZERO.getMaxSpeed(),
            "La velocità massima del vettore zero deve essere 0");
        
        // Test con componenti negative
        Vector negativeVector = new Vector(-4, -2);
        assertEquals(4, negativeVector.getMaxSpeed(),
            "La velocità massima deve considerare il valore assoluto delle componenti");
    }

    @Test
    void testEquals() {
        Vector sameVector = new Vector(dx, dy);
        Vector differentVector = new Vector(dx + 1, dy);

        assertTrue(vector.equals(vector), "Un vettore deve essere uguale a se stesso");
        assertTrue(vector.equals(sameVector), "Vettori con stesse componenti devono essere uguali");
        assertFalse(vector.equals(differentVector), "Vettori con componenti diverse non devono essere uguali");
        assertFalse(vector.equals(null), "Un vettore non deve essere uguale a null");
        assertFalse(vector.equals(new Object()), "Un vettore non deve essere uguale a un oggetto di tipo diverso");
    }

    @Test
    void testHashCode() {
        Vector sameVector = new Vector(dx, dy);
        assertEquals(vector.hashCode(), sameVector.hashCode(),
            "Vettori uguali devono avere lo stesso hashcode");
    }

    @Test
    void testToString() {
        assertEquals("(" + dx + ", " + dy + ")", vector.toString(),
            "Il formato della stringa deve essere (dx, dy)");
    }
}