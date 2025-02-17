package it.unicam.cs.mdp.vectorrace.model.ai.strategies.AStar;

import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar.AStarPathFinder;
import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar.ChebyshevHeuristic;
import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.astar.IHeuristicCalculator;
import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bresenham.BresenhamPathCalculator;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointManager;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointTargetFinder;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.DefaultCheckpointTracker;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.PriorityData;
import it.unicam.cs.mdp.vectorrace.model.ai.services.DefaultReservationService;
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
 * Test per la classe PureAStarStrategy
 */
public class PureAStarStrategyTest {
    private PureAStarStrategy strategy;
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

        // Aggiungi un checkpoint e il traguardo
        grid[2][2] = CellType.CHECKPOINT;
        grid[4][4] = CellType.FINISH;

        // Crea la mappa dei checkpoint
        HashMap<Position, PriorityData> checkpointData = new HashMap<>();
        checkpointData.put(new Position(2, 2), new PriorityData(1, 1));

        // Inizializza il tracciato
        track = new Track(grid, checkpointData);

        // Inizializza le dipendenze
        movementManager = new MovementManager();
        BresenhamPathCalculator pathCalculator = new BresenhamPathCalculator();
        checkpointManager = new CheckpointManager(
                new DefaultCheckpointTracker(),
                new DefaultReservationService(),
                pathCalculator);

        // Inizializza il pathfinder e la strategia
        IHeuristicCalculator heuristic = new ChebyshevHeuristic();
        AStarPathFinder pathFinder = new AStarPathFinder(
                heuristic,
                movementManager,
                checkpointManager);
        CheckpointTargetFinder targetFinder = new CheckpointTargetFinder();
        strategy = new PureAStarStrategy(pathFinder, targetFinder);

        // Crea un giocatore di test
        Position startPos = new Position(0, 0);
        player = new BotPlayer("TestBot", Color.RED, startPos, strategy);

        // Inizializza lo stato del gioco
        List<Player> players = new ArrayList<>();
        players.add(player);
        gameState = new GameState(track, players);
    }

    @Test
    void testGetNextAcceleration() {
        Vector acceleration = strategy.getNextAcceleration(player, gameState);

        // Verifica che l'accelerazione sia valida
        assertNotNull(acceleration);
        assertTrue(Math.abs(acceleration.getDx()) <= 1);
        assertTrue(Math.abs(acceleration.getDy()) <= 1);
    }

    @Test
    void testAccelerationTowardsCheckpoint() {
        Position checkpointPos = new Position(2, 2);

        Vector acceleration = strategy.getNextAcceleration(player, gameState);

        // Verifica che l'accelerazione porti verso il checkpoint
        assertTrue(acceleration.getDx() >= 0); // Il checkpoint è a destra
        assertTrue(acceleration.getDy() >= 0); // Il checkpoint è in basso
    }

    @Test
    void testNoValidPath() {
        // Crea un tracciato senza percorso valido
        CellType[][] grid = new CellType[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = CellType.WALL;
            }
        }
        grid[0][0] = CellType.ROAD; // Solo la posizione del player
        grid[4][4] = CellType.FINISH;

        track = new Track(grid, new HashMap<>());

        List<Player> players = new ArrayList<>();
        players.add(player);
        gameState = new GameState(track, players);

        Vector acceleration = strategy.getNextAcceleration(player, gameState);

        // Dovrebbe restituire un'accelerazione nulla quando non c'è un percorso
        assertEquals(new Vector(0, 0), acceleration);
    }
}