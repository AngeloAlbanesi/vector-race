package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Defines the interface for managing checkpoint reservations in the Vector Race game.
 * Implementations of this interface are responsible for handling reservations
 * to prevent multiple players from targeting the same checkpoint simultaneously.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Reserving checkpoints for specific players</li>
 *   <li>Checking if a checkpoint is already reserved</li>
 *   <li>Removing reservations when they are no longer needed</li>
 * </ul>
 */
public interface IReservationService {
    /**
     * Reserves a checkpoint for a specific player.
     *
     * @param checkpoint The position of the checkpoint.
     * @param playerName The name of the player making the reservation.
     */
    void reserveCheckpoint(Position checkpoint, String playerName);

    /**
     * Checks if a checkpoint is already reserved by another player.
     *
     * @param checkpoint The position of the checkpoint.
     * @param playerName The name of the player requesting the check.
     * @return true if the checkpoint is reserved by another player, false otherwise.
     */
    boolean isCheckpointReserved(Position checkpoint, String playerName);

    /**
     * Removes the reservation for a checkpoint made by a specific player.
     *
     * @param checkpoint The position of the checkpoint.
     * @param playerName The name of the player whose reservation should be removed.
     */
    void removeReservation(Position checkpoint, String playerName);
}