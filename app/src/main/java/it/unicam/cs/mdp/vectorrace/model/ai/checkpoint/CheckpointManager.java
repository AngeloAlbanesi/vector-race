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
 * Classe che gestisce il controllo e la validazione dei checkpoint nel gioco.
 * Utilizza il pattern Strategy per delegare le responsabilità specifiche.
 */
public class CheckpointManager {
    private final ICheckpointTracker checkpointTracker;
    private final IReservationService reservationService;
    private final BresenhamPathCalculator pathCalculator;

    /**
     * Costruttore che inizializza il manager con le sue dipendenze.
     *
     * @param checkpointTracker  gestore del tracciamento dei checkpoint
     * @param reservationService gestore delle prenotazioni dei checkpoint
     * @param pathCalculator     calcolatore del percorso tra punti
     */
    public CheckpointManager(ICheckpointTracker checkpointTracker,
            IReservationService reservationService,
            BresenhamPathCalculator pathCalculator) {
        this.checkpointTracker = checkpointTracker;
        this.reservationService = reservationService;
        this.pathCalculator = pathCalculator;
    }

    /**
     * Costruttore che crea un'istanza con implementazioni predefinite.
     */
    public CheckpointManager() {
        this(new DefaultCheckpointTracker(),
                new DefaultReservationService(),
                new BresenhamPathCalculator());
    }

    /**
     * Verifica i checkpoint attraversati durante il movimento di un giocatore.
     *
     * @param player giocatore che si sta muovendo
     * @param oldPos posizione di partenza
     * @param newPos posizione di arrivo
     * @param track  tracciato di gioco
     */
    public void checkCrossedCheckpoints(Player player, Position oldPos, Position newPos, Track track) {
        List<Position> path = pathCalculator.calculatePath(oldPos, newPos);
        processCheckpointsAlongPath(player, path, track);
    }

    /**
     * Elabora i checkpoint lungo il percorso del giocatore.
     */
    private void processCheckpointsAlongPath(Player player, List<Position> path, Track track) {
        for (Position currentPos : path) {
            if (isCheckpoint(currentPos, track)) {
                processCheckpoint(player, currentPos, track);
            }
        }
    }

    /**
     * Verifica se una posizione contiene un checkpoint.
     */
    private boolean isCheckpoint(Position position, Track track) {
        return track.getCell(position.getX(), position.getY()) == CellType.CHECKPOINT;
    }

    /**
     * Elabora un singolo checkpoint per un giocatore.
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
     * Prenota un checkpoint per un giocatore.
     */
    public void reserveCheckpoint(Position checkpoint, String playerName) {
        reservationService.reserveCheckpoint(checkpoint, playerName);
    }

    /**
     * Verifica se un checkpoint è già prenotato da un altro giocatore.
     */
    public boolean isCheckpointReserved(Position checkpoint, String playerName) {
        return reservationService.isCheckpointReserved(checkpoint, playerName);
    }

    /**
     * Verifica se un giocatore ha già attraversato un checkpoint.
     */
    public boolean hasPassedCheckpoint(String playerName, Position checkpoint) {
        return checkpointTracker.hasPassedCheckpoint(playerName, checkpoint);
    }
}