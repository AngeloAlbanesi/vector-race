package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs;

import it.unicam.cs.mdp.vectorrace.model.ai.services.IMoveValidator;
import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

/**
 * Test per la classe BFSExecutor
 */
public class BFSExecutorTest {
    private BFSExecutor bfsExecutor;
    private Track track;
    private IMoveValidator moveValidator;

    @BeforeEach
    void setUp() {
        // Creo un move validator mock che accetta tutte le mosse tranne quelle verso i
        // muri
        moveValidator = new IMoveValidator() {
            @Override
            public boolean validateTempMove(Position startPos, Vector velocity, Track track) {
                Position endPos = startPos.move(velocity);
                return track.isWithinBounds(endPos.getX(), endPos.getY()) &&
                        track.getCell(endPos.getX(), endPos.getY()) != CellType.WALL;
            }

            @Override
            public boolean validateRealMove(Player player, Vector acceleration, GameState gameState) {
                // Per i test usiamo solo validateTempMove
                return true;
            }
        };

        bfsExecutor = new BFSExecutor(moveValidator);

        // Crea una griglia 5x5 per il test
        CellType[][] grid = new CellType[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = CellType.ROAD;
            }
        }

        // Crea un muro nel mezzo per testare il pathfinding
        grid[2][2] = CellType.WALL;

        track = new Track(grid, new HashMap<>());
    }

    @Test
    void testFindPathToReachableTarget() {
        Position start = new Position(0, 0);
        Vector startVelocity = new Vector(0, 0);
        Position target = new Position(4, 4);

        BFSSearchResult result = bfsExecutor.search(start, startVelocity, target, track);

        // Verifica che sia stato trovato un percorso
        assertTrue(result.isFound());
        assertNotNull(result.getNextAcceleration());

        // Verifica che l'accelerazione sia valida (-1, 0, 1)
        assertTrue(Math.abs(result.getNextAcceleration().getDx()) <= 1);
        assertTrue(Math.abs(result.getNextAcceleration().getDy()) <= 1);
    }

    @Test
    void testMaxSpeedLimit() {
        Position start = new Position(0, 0);
        Vector startVelocity = new Vector(3, 3); // Velocità vicina al limite
        Position target = new Position(4, 4);

        BFSSearchResult result = bfsExecutor.search(start, startVelocity, target, track);

        // Verifica che il risultato rispetti il limite di velocità
        Vector newVelocity = startVelocity.add(result.getNextAcceleration());
        assertTrue(Math.abs(newVelocity.getDx()) <= 4); // MAX_SPEED è 4
        assertTrue(Math.abs(newVelocity.getDy()) <= 4);
    }

    @Test
    void testUnreachableTarget() {
        // Crea una griglia con un muro che blocca completamente il percorso
        CellType[][] grid = new CellType[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = CellType.WALL;
            }
        }
        grid[0][0] = CellType.ROAD; // Solo la posizione iniziale è percorribile

        track = new Track(grid, new HashMap<>());

        Position start = new Position(0, 0);
        Vector startVelocity = new Vector(0, 0);
        Position target = new Position(4, 4);

        BFSSearchResult result = bfsExecutor.search(start, startVelocity, target, track);

        // Verifica che non sia stato trovato un percorso
        assertFalse(result.isFound());
        assertEquals(new Vector(0, 0), result.getNextAcceleration());
    }

    @Test
    void testPathToAdjacentTarget() {
        Position start = new Position(0, 0);
        Vector startVelocity = new Vector(0, 0);
        Position target = new Position(1, 1);

        BFSSearchResult result = bfsExecutor.search(start, startVelocity, target, track);

        // Verifica che sia stato trovato un percorso
        assertTrue(result.isFound());

        // Verifica che l'accelerazione sia quella minima necessaria
        Vector acc = result.getNextAcceleration();
        assertTrue(acc.getDx() > 0);
        assertTrue(acc.getDy() > 0);
        assertEquals(1, Math.abs(acc.getDx()));
        assertEquals(1, Math.abs(acc.getDy()));
    }
}