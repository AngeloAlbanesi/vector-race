package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;

/**
 * Interfaccia che definisce le operazioni per localizzare la cella di arrivo nel tracciato.
 */
public interface IFinishLocator {
    /**
     * Cerca e restituisce la posizione della cella di arrivo nel tracciato.
     * 
     * @param track Il tracciato in cui cercare la cella di arrivo
     * @return La posizione della cella di arrivo, o null se non trovata
     */
    Position locateFinish(Track track);
}