package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Adapts the {@link MovementManager} to implement the {@link IMoveValidator} interface.
 * This adapter allows the AI strategies to use the existing movement validation
 * logic in the game, providing a consistent way to check move validity.
 *
 * <p>This adapter enables:
 * <ul>
 *   <li>Reuse of existing movement validation logic</li>
 *   <li>Simplified integration of AI strategies</li>
 *   <li>Consistent move validation across the game</li>
 * </ul>
 */
public class MovementValidatorAdapter implements IMoveValidator {
    private final MovementManager movementManager;

    /**
     * Creates a new MovementValidatorAdapter.
     * Initializes the adapter with a new instance of {@link MovementManager}.
     */
    public MovementValidatorAdapter() {
        this.movementManager = new MovementManager();
    }

    @Override
    public boolean validateTempMove(Position start, Vector velocity, Track track) {
        return movementManager.validateMoveTemp(start, velocity, track);
    }

    @Override
    public boolean validateRealMove(Player player, Vector acceleration, GameState gameState) {
        return movementManager.validateMove(player, acceleration, gameState);
    }
}