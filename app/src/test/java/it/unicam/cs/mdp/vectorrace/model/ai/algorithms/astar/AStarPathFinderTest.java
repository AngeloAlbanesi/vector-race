package it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar;

import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointManager;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.DefaultCheckpointTracker;
import it.unicam.cs.mdp.vectorrace.model.ai.services.DefaultReservationService;
import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bresenham.BresenhamPathCalculator;
import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.BotPlayer;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Test per la classe AStarPathFinder
 */
public class AStarPathFinderTest {
    private AStarPathFinder pathFinder;
    private Track track;
    private BotPlayer player;
    private GameState gameState;
    private MovementManager movementManager;
    private CheckpointManager checkpointManager;

    @BeforeEach
    void setUp() {
        // Crea una griglia 5x5 per il test
        CellType[][] grid = new CellType[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = CellType.ROAD;
            }
        }
        
        // Crea un muro nel mezzo per testare il pathfinding
        grid[2][2] = CellType.WALL;
        
        // Inizializza il tracciato
        track = new Track(grid, new HashMap<>());
        
        // Inizializza le dipendenze
        movementManager = new MovementManager();
        checkpointManager = new CheckpointManager(
            new DefaultCheckpointTracker(),
            new DefaultReservationService(),
            new BresenhamPathCalculator()
        );
        
        // Inizializza il pathfinder con euristica di Chebyshev
        pathFinder = new AStarPathFinder(
            new ChebyshevHeuristic(),
            movementManager,
            checkpointManager
        );
        
        // Crea un giocatore di test
        Position startPos = new Position(0, 0);
        player = new BotPlayer("TestBot", Color.RED, startPos, null);
        
        // Inizializza lo stato del gioco
        List<Player> players = new ArrayList<>();
        players.add(player);
        gameState = new GameState(track, players);
    }

    @Test
    void testFindPathToReachableTarget() {
        // Target in basso a destra, raggiungibile aggirando il muro
        Position target = new Position(4, 4);
        
        Vector acceleration = pathFinder.findPath(player, gameState, target);
        
        // Verifica che sia stata trovata un'accelerazione valida
        assertNotNull(acceleration);
        assertTrue(Math.abs(acceleration.getDx()) <= 1);
        assertTrue(Math.abs(acceleration.getDy()) <= 1);
    }

    @Test
    void testMaxSpeedLimit() {
        // Imposta una velocità alta al giocatore
        player.updateVelocity(new Vector(4, 4));
        Position target = new Position(4, 4);
        
        Vector acceleration = pathFinder.findPath(player, gameState, target);
        
        // Verifica che l'accelerazione mantenga la velocità sotto il limite
        Vector newVelocity = player.getVelocity().add(acceleration);
        assertTrue(Math.abs(newVelocity.getDx()) <= 5);
        assertTrue(Math.abs(newVelocity.getDy()) <= 5);
    }

    @Test
    void testNullTarget() {
        Vector acceleration = pathFinder.findPath(player, gameState, null);
        
        // Dovrebbe restituire un vettore zero quando il target è null
        assertEquals(new Vector(0, 0), acceleration);
    }

    @Test
    void testUnreachableTarget() {
        // Crea un perimetro di muri intorno al giocatore
        Position playerPos = player.getPosition();
        CellType[][] grid = new CellType[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = CellType.WALL;
            }
        }
        grid[playerPos.getX()][playerPos.getY()] = CellType.ROAD;
        track = new Track(grid, new HashMap<>());
        
        List<Player> players = new ArrayList<>();
        players.add(player);
        gameState = new GameState(track, players);
        
        Position target = new Position(4, 4);
        Vector acceleration = pathFinder.findPath(player, gameState, target);
        
        // Dovrebbe restituire un vettore zero quando il target è irraggiungibile
        assertEquals(new Vector(0, 0), acceleration);
    }
}