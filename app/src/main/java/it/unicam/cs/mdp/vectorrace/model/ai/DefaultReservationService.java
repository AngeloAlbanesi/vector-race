package it.unicam.cs.mdp.vectorrace.model.ai;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementazione predefinita dell'interfaccia IReservationService.
 * Gestisce le prenotazioni dei checkpoint per i giocatori.
 */
public class DefaultReservationService implements IReservationService {
    private final Map<Position, String> checkpointReservations;

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