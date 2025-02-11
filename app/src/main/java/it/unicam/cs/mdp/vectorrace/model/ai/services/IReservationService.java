package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Interfaccia per la gestione delle prenotazioni dei checkpoint.
 */
public interface IReservationService {
    /**
     * Prenota un checkpoint per un giocatore specifico.
     * 
     * @param checkpoint posizione del checkpoint
     * @param playerName nome del giocatore
     */
    void reserveCheckpoint(Position checkpoint, String playerName);

    /**
     * Verifica se un checkpoint è già prenotato da un altro giocatore.
     * 
     * @param checkpoint posizione del checkpoint
     * @param playerName nome del giocatore che richiede la verifica
     * @return true se il checkpoint è prenotato da un altro giocatore
     */
    boolean isCheckpointReserved(Position checkpoint, String playerName);

    /**
     * Rimuove la prenotazione di un checkpoint per un giocatore specifico.
     * 
     * @param checkpoint posizione del checkpoint
     * @param playerName nome del giocatore
     */
    void removeReservation(Position checkpoint, String playerName);
}