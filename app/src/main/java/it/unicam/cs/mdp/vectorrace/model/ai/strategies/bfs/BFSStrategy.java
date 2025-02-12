package it.unicam.cs.mdp.vectorrace.model.ai.strategies.bfs;

import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bfs.BFSExecutor;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointManager;
import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.CheckpointTargetFinder;
import it.unicam.cs.mdp.vectorrace.model.ai.services.IMoveValidator;
import it.unicam.cs.mdp.vectorrace.model.ai.services.MovementValidatorAdapter;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Implementazione della strategia BFS che utilizza componenti specializzati
 * per gestire le varie responsabilità dell'algoritmo.
 */
public class BFSStrategy implements AIStrategy {

    private final BFSExecutor bfsExecutor;
    private final IMoveValidator moveValidator;
    private final CheckpointTargetFinder targetFinder;
    private final CheckpointManager checkpointManager;

    /**
     * Costruttore con dependency injection per tutti i componenti necessari.
     */
    public BFSStrategy(BFSExecutor bfsExecutor,
            IMoveValidator moveValidator,
            CheckpointTargetFinder targetFinder,
            CheckpointManager checkpointManager) {
        this.bfsExecutor = bfsExecutor;
        this.moveValidator = moveValidator;
        this.targetFinder = targetFinder;
        this.checkpointManager = checkpointManager;
    }

    /**
     * Costruttore di default che inizializza i componenti con le implementazioni
     * standard.
     */
    public BFSStrategy() {
        this.moveValidator = new MovementValidatorAdapter();
        this.bfsExecutor = new BFSExecutor(moveValidator);
        this.targetFinder = new CheckpointTargetFinder();
        this.checkpointManager = new CheckpointManager();
    }

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        Position target = findTargetPosition(player, gameState);
        if (target == null) {
            return Vector.ZERO;
        }

        Vector acceleration = calculateAcceleration(player, target, gameState);
        return processCalculatedAcceleration(player, acceleration, gameState);
    }

    private Position findTargetPosition(Player player, GameState gameState) {
        return targetFinder.findNextTarget(player, gameState);
    }

    private Vector calculateAcceleration(Player player, Position target, GameState gameState) {
        BFSExecutor.SearchResult result = bfsExecutor.search(
                player.getPosition(),
                player.getVelocity(),
                target,
                gameState.getTrack());

        return result.isFound() ? result.getNextAcceleration() : Vector.ZERO;
    }

    private Vector processCalculatedAcceleration(Player player, Vector acceleration, GameState gameState) {
        return validateFinalMove(player, acceleration, player.getPosition(), gameState)
                ? acceleration
                : Vector.ZERO;
    }

    /**
     * Valida la mossa finale e aggiorna i checkpoint attraversati.
     */
    private boolean validateFinalMove(Player player, Vector acceleration,
            Position startPosition, GameState gameState) {
        Vector finalVelocity = player.getVelocity().add(acceleration);
        Position finalPosition = player.getPosition().move(finalVelocity);

        if (moveValidator.validateRealMove(player, acceleration, gameState)) {
            checkpointManager.checkCrossedCheckpoints(
                    player,
                    startPosition,
                    finalPosition,
                    gameState.getTrack());
            return true;
        }

        return false;
    }
}
