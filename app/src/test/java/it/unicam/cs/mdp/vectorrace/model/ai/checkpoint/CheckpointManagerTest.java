package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bresenham.BresenhamPathCalculator;
import it.unicam.cs.mdp.vectorrace.model.ai.services.DefaultReservationService;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.BotPlayer;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Test per la classe CheckpointManager
 */
public class CheckpointManagerTest {
    private CheckpointManager manager;
    private ICheckpointTracker tracker;
    private DefaultReservationService reservationService;
    private BresenhamPathCalculator pathCalculator;
    private Track track;
    private BotPlayer player;

    @BeforeEach
    void setUp() {
        // Inizializza le dipendenze
        tracker = new DefaultCheckpointTracker();
        reservationService = new DefaultReservationService();
        pathCalculator = new BresenhamPathCalculator();
        manager = new CheckpointManager(tracker, reservationService, pathCalculator);
        
        // Crea una griglia 5x5 per il test
        CellType[][] grid = new CellType[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = CellType.ROAD;
            }
        }
        Position checkpointPos = new Position(2, 2);
        grid[2][2] = CellType.CHECKPOINT;
        
        // Crea la mappa dei checkpoint
        Map<Position, PriorityData> checkpointData = new HashMap<>();
        // priorityLevel=1 (bassa priorità), checkpointNumber=1
        checkpointData.put(checkpointPos, new PriorityData(1, 1));
        
        // Inizializza il tracciato
        track = new Track(grid, checkpointData);

        // Crea un AIStrategy mock per il BotPlayer
        AIStrategy mockStrategy = new AIStrategy() {
            @Override
            public Vector getNextAcceleration(Player player, GameState gameState) {
                return new Vector(0, 0);
            }
        };
        
        // Crea un BotPlayer per i test
        Position startPos = new Position(1, 1);
        player = new BotPlayer("TestBot", Color.RED, startPos, mockStrategy);
    }


    @Test
    void testCheckpointPassing() {
        Position checkpoint = new Position(2, 2);
        
        // Verifica che inizialmente il checkpoint non sia stato passato
        assertFalse(manager.hasPassedCheckpoint(player.getName(), checkpoint));
        
        // Simula il passaggio del checkpoint
        Position startPos = new Position(1, 1);
        Position endPos = new Position(3, 3);
        manager.checkCrossedCheckpoints(player, startPos, endPos, track);
        
        // Verifica che il checkpoint sia stato registrato come passato
        assertTrue(manager.hasPassedCheckpoint(player.getName(), checkpoint));
    }

    @Test
    void testCheckpointSequence() {
        Position checkpoint = new Position(2, 2);
        Position startPos = new Position(1, 1);
        Position endPos = new Position(3, 3);
        
        // Prima attraversa il checkpoint
        manager.checkCrossedCheckpoints(player, startPos, endPos, track);
        
        // Verifica che il checkpoint sia stato passato
        assertTrue(manager.hasPassedCheckpoint(player.getName(), checkpoint));
        
        // Verifica che la prenotazione sia stata rimossa dopo il passaggio
        assertFalse(manager.isCheckpointReserved(checkpoint, player.getName()));
    }
}