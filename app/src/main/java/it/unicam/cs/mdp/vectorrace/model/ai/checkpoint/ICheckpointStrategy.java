package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import java.util.Set;
import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Interfaccia che definisce la strategia per selezionare il checkpoint più appropriato
 * tra quelli disponibili per un dato livello.
 */
public interface ICheckpointStrategy {
    /**
     * Seleziona il checkpoint più appropriato tra quelli disponibili,
     * in base alla posizione corrente.
     * 
     * @param checkpoints L'insieme dei checkpoint disponibili
     * @param currentPosition La posizione corrente da cui calcolare la selezione
     * @return Il checkpoint selezionato, o null se nessun checkpoint è disponibile
     */
    Position selectCheckpoint(Set<Position> checkpoints, Position currentPosition);
}