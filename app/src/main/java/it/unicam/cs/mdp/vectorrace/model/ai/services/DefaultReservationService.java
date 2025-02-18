package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the {@link IReservationService} interface.
 * This class manages checkpoint reservations for players in the Vector Race game,
 * preventing multiple players from targeting the same checkpoint simultaneously.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Reserving checkpoints for players</li>
 *   <li>Checking if a checkpoint is already reserved</li>
 *   <li>Removing reservations when a player passes a checkpoint</li>
 * </ul>
 */
public class DefaultReservationService implements IReservationService {
    private final Map<Position, String> checkpointReservations;

    /**
     * Creates a new DefaultReservationService.
     * Initializes the data structures used to store checkpoint reservations.
     */
    public DefaultReservationService() {
        this.checkpointReservations = new HashMap<>();
    }

    @Override
    public void reserveCheckpoint(Position checkpoint, String playerName) {
        checkpointReservations.put(checkpoint, playerName);
    }

    @Override
    public boolean isCheckpointReserved(Position checkpoint, String playerName) {
        String reservedBy = checkpointReservations.get(checkpoint);
        return reservedBy != null && !reservedBy.equals(playerName);
    }

    @Override
    public void removeReservation(Position checkpoint, String playerName) {
        if (checkpointReservations.containsKey(checkpoint) && 
            checkpointReservations.get(checkpoint).equals(playerName)) {
            checkpointReservations.remove(checkpoint);
        }
    }
}