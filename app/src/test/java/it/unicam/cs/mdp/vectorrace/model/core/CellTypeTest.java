package it.unicam.cs.mdp.vectorrace.model.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test unitari per l'enum CellType.
 */
public class CellTypeTest {

    @Test
    void testFromCharWall() {
        assertEquals(CellType.WALL, CellType.fromChar('#'), 
            "Il carattere '#' deve corrispondere a WALL");
    }

    @Test
    void testFromCharRoad() {
        assertEquals(CellType.ROAD, CellType.fromChar('.'), 
            "Il carattere '.' deve corrispondere a ROAD");
    }

    @Test
    void testFromCharStart() {
        assertEquals(CellType.START, CellType.fromChar('S'), 
            "Il carattere 'S' deve corrispondere a START");
    }

    @Test
    void testFromCharFinish() {
        assertEquals(CellType.FINISH, CellType.fromChar('*'), 
            "Il carattere '*' deve corrispondere a FINISH");
    }

    @ParameterizedTest
    @ValueSource(chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'})
    void testFromCharCheckpoint(char digit) {
        assertEquals(CellType.CHECKPOINT, CellType.fromChar(digit), 
            "Ogni carattere numerico deve corrispondere a CHECKPOINT");
    }

    @Test
    void testFromCharDefaultCase() {
        assertEquals(CellType.ROAD, CellType.fromChar(' '), 
            "Un carattere non riconosciuto deve corrispondere a ROAD");
        assertEquals(CellType.ROAD, CellType.fromChar('x'), 
            "Un carattere non riconosciuto deve corrispondere a ROAD");
    }
}