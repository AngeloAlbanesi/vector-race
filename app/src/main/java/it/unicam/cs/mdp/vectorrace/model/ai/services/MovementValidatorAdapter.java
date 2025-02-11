package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Adapter per il MovementManager che implementa l'interfaccia IMoveValidator.
 */
public class MovementValidatorAdapter implements IMoveValidator {
    private final MovementManager movementManager;

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