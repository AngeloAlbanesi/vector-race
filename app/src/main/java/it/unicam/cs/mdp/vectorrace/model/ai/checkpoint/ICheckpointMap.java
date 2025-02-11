package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

import java.util.Set;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;

/**
 * Interfaccia che definisce le operazioni per la gestione della mappa dei checkpoint.
 */
public interface ICheckpointMap {
    /**
     * Inizializza la mappa dei checkpoint per il tracciato specificato.
     * 
     * @param track Il tracciato da cui estrarre i checkpoint
     */
    void initialize(Track track);

    /**
     * Restituisce l'insieme delle posizioni dei checkpoint per il livello specificato.
     * 
     * @param level Il livello del checkpoint
     * @return Set di posizioni dei checkpoint per quel livello, può essere vuoto
     */
    Set<Position> getCheckpoints(int level);
}