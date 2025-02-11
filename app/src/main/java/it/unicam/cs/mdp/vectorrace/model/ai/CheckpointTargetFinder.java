package it.unicam.cs.mdp.vectorrace.model.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Classe che determina la cella "target" (il checkpoint successivo o il
 * traguardo)
 * per un giocatore. Utilizza strategie specifiche per la gestione dei
 * checkpoint
 * e la ricerca del traguardo.
 */
public class CheckpointTargetFinder {

    private final ICheckpointMap checkpointMap;
    private final IFinishLocator finishLocator;
    private final ICheckpointStrategy checkpointStrategy;
    private final Map<String, Position> playerTargets;

    /**
     * Costruisce un nuovo CheckpointTargetFinder con le strategie specificate.
     * 
     * @param checkpointMap      Gestore della mappa dei checkpoint
     * @param finishLocator      Localizzatore della cella di arrivo
     * @param checkpointStrategy Strategia di selezione dei checkpoint
     */
    public CheckpointTargetFinder(
            ICheckpointMap checkpointMap,
            IFinishLocator finishLocator,
            ICheckpointStrategy checkpointStrategy) {
        this.checkpointMap = checkpointMap;
        this.finishLocator = finishLocator;
        this.checkpointStrategy = checkpointStrategy;
        this.playerTargets = new HashMap<>();
    }

    /**
     * Costruttore di default che utilizza le implementazioni standard.
     */
    public CheckpointTargetFinder() {
        this(new CheckpointMapManager(),
                new FinishCellLocator(),
                new NearestCheckpointStrategy());
    }

    /**
     * Restituisce la prossima posizione di checkpoint (o finish) che il giocatore
     * deve raggiungere.
     */
    public Position findNextTarget(Player player, GameState gameState) {
        Track track = gameState.getTrack();
        int currentCheckpointIndex = player.getNextCheckpointIndex();

        // Inizializza o aggiorna la mappa dei checkpoint se necessario
        checkpointMap.initialize(track);

        // Verifica se dobbiamo puntare al traguardo
        if (shouldTargetFinish(currentCheckpointIndex, track)) {
            return finishLocator.locateFinish(track);
        }

        // Ottieni i checkpoint disponibili per il livello corrente
        Set<Position> availableCheckpoints = checkpointMap.getCheckpoints(currentCheckpointIndex);

        // Seleziona il checkpoint appropriato
        Position target = selectTarget(player, availableCheckpoints);
        return target != null ? target : finishLocator.locateFinish(track);
    }

    /**
     * Determina se il giocatore deve puntare al traguardo.
     */
    private boolean shouldTargetFinish(int currentCheckpointIndex, Track track) {
        return currentCheckpointIndex > track.getMaxCheckpoint() ||
                checkpointMap.getCheckpoints(currentCheckpointIndex).isEmpty();
    }

    /**
     * Seleziona il target appropriato tra i checkpoint disponibili.
     */
    private Position selectTarget(Player player, Set<Position> availableCheckpoints) {
        // Verifica se il target corrente è ancora valido
        Position currentTarget = playerTargets.get(player.getName());
        if (currentTarget != null && availableCheckpoints.contains(currentTarget)) {
            return currentTarget;
        }

        // Seleziona un nuovo target usando la strategia configurata
        Position newTarget = checkpointStrategy.selectCheckpoint(
                availableCheckpoints,
                player.getPosition());

        // Memorizza e restituisce il nuovo target
        if (newTarget != null) {
            playerTargets.put(player.getName(), newTarget);
        }
        return newTarget;
    }
}
