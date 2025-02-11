package model;

import org.junit.jupiter.api.Test;

import it.unicam.cs.mdp.vectorrace.model.ai.state.VectorRacePathState;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class VectorRacePathStateTest {

    @Test
    public void testEqualsAndHashCode() {
        VectorRacePathState state1 = new VectorRacePathState(new Position(1, 2), new Vector(3, 4), null);
        VectorRacePathState state2 = new VectorRacePathState(new Position(1, 2), new Vector(3, 4), null);
        VectorRacePathState state3 = new VectorRacePathState(new Position(5, 6), new Vector(7, 8), null);

        assertEquals(state1, state2);
        assertNotEquals(state1, state3);
        assertEquals(state1.hashCode(), state2.hashCode());
        assertNotEquals(state1.hashCode(), state3.hashCode());
    }

    @Test
    public void testGettersAndSetters(){
        VectorRacePathState state = new VectorRacePathState(new Position(1,2), new Vector(3,4), null);
        state.setG(10.0);
        state.setH(20.0);

        assertEquals(10.0, state.getG());
        assertEquals(20.0, state.getH());
    }

    @Test
    public void testCompareTo(){
        VectorRacePathState state1 = new VectorRacePathState(new Position(1, 2), new Vector(3, 4), null);
        state1.setG(10.0);
        state1.setH(5.0); // F = 15

        VectorRacePathState state2 = new VectorRacePathState(new Position(1, 2), new Vector(3, 4), null);
        state2.setG(5.0);
        state2.setH(20.0); // F = 25

        VectorRacePathState state3 = new VectorRacePathState(new Position(5, 6), new Vector(7, 8), null);
        state3.setG(10.0);
        state3.setH(10.0); // F = 20
        
        assertTrue(state1.compareTo(state2) < 0);
        assertTrue(state2.compareTo(state1) > 0);
        assertTrue(state1.compareTo(state3) < 0);
        assertTrue(state3.compareTo(state1) > 0);
        assertTrue(state2.compareTo(state3) > 0);
        assertTrue(state3.compareTo(state2) < 0);

    }
}