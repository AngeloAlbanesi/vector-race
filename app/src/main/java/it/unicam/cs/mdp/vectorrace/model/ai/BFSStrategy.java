package it.unicam.cs.mdp.vectorrace.model.ai;

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
        Position currentPosition = player.getPosition();
        Vector currentVelocity = player.getVelocity();

        // Trova il prossimo checkpoint target
        Position target = targetFinder.findNextTarget(player, gameState);
        if (target == null) {
            return new Vector(0, 0);
        }

        // Esegui la ricerca BFS
        BFSExecutor.SearchResult result = bfsExecutor.search(
                currentPosition,
                currentVelocity,
                target,
                gameState.getTrack());

        // Se non è stato trovato un percorso valido
        if (!result.isFound()) {
            return new Vector(0, 0);
        }

        Vector chosenAcceleration = result.getNextAcceleration();

        // Valida la mossa finale e aggiorna i checkpoint se necessario
        if (validateFinalMove(player, chosenAcceleration, currentPosition, gameState)) {
            return chosenAcceleration;
        }

        // Fallback: ritorna accelerazione nulla
        return new Vector(0, 0);
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
