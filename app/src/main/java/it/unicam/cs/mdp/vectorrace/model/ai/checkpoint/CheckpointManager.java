package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import it.unicam.cs.mdp.vectorrace.model.ai.algorithms.bresenham.BresenhamPathCalculator;
import it.unicam.cs.mdp.vectorrace.model.ai.services.DefaultReservationService;
import it.unicam.cs.mdp.vectorrace.model.ai.services.IReservationService;
import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import java.util.List;

/**
 * Manages checkpoint control and validation in the Vector Race game.
 * This class uses the Strategy pattern to delegate specific responsibilities
 * related to checkpoint tracking and reservation.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Verifying checkpoint crossings during player movement</li>
 *   <li>Managing checkpoint reservations to avoid conflicts</li>
 *   <li>Tracking which checkpoints have been passed by each player</li>
 * </ul>
 *
 * <p>Components:
 * <ul>
 *   <li>{@link ICheckpointTracker} - Tracks which checkpoints have been passed</li>
 *   <li>{@link IReservationService} - Manages checkpoint reservations</li>
 *   <li>{@link BresenhamPathCalculator} - Calculates the path between positions</li>
 * </ul>
 */
public class CheckpointManager {
    private final ICheckpointTracker checkpointTracker;
    private final IReservationService reservationService;
    private final BresenhamPathCalculator pathCalculator;

    /**
     * Creates a new CheckpointManager with specified dependencies.
     *
     * @param checkpointTracker The checkpoint tracker implementation.
     * @param reservationService The reservation service implementation.
     * @param pathCalculator The path calculator implementation.
     */
    public CheckpointManager(ICheckpointTracker checkpointTracker,
            IReservationService reservationService,
            BresenhamPathCalculator pathCalculator) {
        this.checkpointTracker = checkpointTracker;
        this.reservationService = reservationService;
        this.pathCalculator = pathCalculator;
    }

    /**
     * Creates a new CheckpointManager with default implementations.
     * Uses default implementations for checkpoint tracking and reservation.
     */
    public CheckpointManager() {
        this(new DefaultCheckpointTracker(),
                new DefaultReservationService(),
                new BresenhamPathCalculator());
    }

    /**
     * Checks which checkpoints have been crossed during a player's movement.
     *
     * @param player The player that is moving.
     * @param oldPos The starting position.
     * @param newPos The ending position.
     * @param track The game track.
     */
    public void checkCrossedCheckpoints(Player player, Position oldPos, Position newPos, Track track) {
        List<Position> path = pathCalculator.calculatePath(oldPos, newPos);
        processCheckpointsAlongPath(player, path, track);
    }

    /**
     * Processes checkpoints along the player's path.
     *
     * @param player The player moving along the path.
     * @param path The list of positions representing the path.
     * @param track The game track.
     */
    private void processCheckpointsAlongPath(Player player, List<Position> path, Track track) {
        for (Position currentPos : path) {
            if (isCheckpoint(currentPos, track)) {
                processCheckpoint(player, currentPos, track);
            }
        }
    }

    /**
     * Checks if a position contains a checkpoint.
     *
     * @param position The position to check.
     * @param track The game track.
     * @return true if the position is a checkpoint, false otherwise.
     */
    private boolean isCheckpoint(Position position, Track track) {
        return track.getCell(position.getX(), position.getY()) == CellType.CHECKPOINT;
    }

    /**
     * Processes a single checkpoint for a player.
     *
     * @param player The player crossing the checkpoint.
     * @param checkpoint The checkpoint position.
     * @param track The game track.
     */
    private void processCheckpoint(Player player, Position checkpoint, Track track) {
        int checkpointNum = track.getCheckpointNumber(checkpoint);
        if (checkpointNum == player.getNextCheckpointIndex()) {
            checkpointTracker.markCheckpointAsPassed(player.getName(), checkpoint);
            player.incrementCheckpointIndex();
            reservationService.removeReservation(checkpoint, player.getName());
        }
    }

    /**
     * Reserves a checkpoint for a player.
     *
     * @param checkpoint The checkpoint position.
     * @param playerName The name of the player reserving the checkpoint.
     */
    public void reserveCheckpoint(Position checkpoint, String playerName) {
        reservationService.reserveCheckpoint(checkpoint, playerName);
    }

    /**
     * Checks if a checkpoint is already reserved by another player.
     *
     * @param checkpoint The checkpoint position.
     * @param playerName The name of the player attempting to reserve the checkpoint.
     * @return true if the checkpoint is reserved by another player, false otherwise.
     */
    public boolean isCheckpointReserved(Position checkpoint, String playerName) {
        return reservationService.isCheckpointReserved(checkpoint, playerName);
    }

    /**
     * Checks if a player has already passed a checkpoint.
     *
     * @param playerName The name of the player.
     * @param checkpoint The checkpoint position.
     * @return true if the player has passed the checkpoint, false otherwise.
     */
    public boolean hasPassedCheckpoint(String playerName, Position checkpoint) {
        return checkpointTracker.hasPassedCheckpoint(playerName, checkpoint);
    }
}