/*
 *   Copyright (c) 2025 ...
 */
package it.unicam.cs.mdp.vectorrace.model;

import java.util.Random;

/**
 * Strategia che utilizza una variante di A* per trovare il percorso ottimale
 * attraverso i checkpoint.
 */
public class SimpleStrategy implements AIStrategy {
    private final PathFinder pathFinder;
    private final CheckpointTargetFinder targetFinder;
    private final MovementManager movementManager;
    private final PlayerStateTracker stateTracker;
    private final CheckpointManager checkpointManager;
    private final Random random = new Random();

    public SimpleStrategy() {
        this.pathFinder = new PathFinder();
        this.targetFinder = new CheckpointTargetFinder();
        this.movementManager = new MovementManager();
        this.stateTracker = new PlayerStateTracker();
        this.checkpointManager = new CheckpointManager();
    }

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        String playerName = player.getName();
        Vector currentVelocity = player.getVelocity();
        Position currentPosition = player.getPosition();

        // Verifica se il giocatore è bloccato
        if (stateTracker.isPlayerStuck(playerName, currentVelocity)) {
            return new Vector(random.nextInt(3) - 1, random.nextInt(3) - 1);
        }

        // Aggiorna lo stato del giocatore
        stateTracker.updatePlayerHistory(playerName, currentPosition);

        // Trova il prossimo target
        Position target = targetFinder.findNextTarget(player, gameState);
        if (target == null) {
            return new Vector(0, 0);
        }

        // Prova con A*
        VectorRacePathState bestPath = pathFinder.findPath(player, target, gameState);
        if (bestPath != null && bestPath.getParent() != null) {
            Vector chosenAcc = bestPath.getVelocity().subtract(currentVelocity);
            if (movementManager.validateMove(player, chosenAcc, gameState)) {
                checkpointManager.checkCrossedCheckpoints(player, currentPosition,
                        currentPosition.move(currentVelocity.add(chosenAcc)), gameState.getTrack());
                return chosenAcc;
            }
        }

        return new Vector(0, 0);
    }
}
