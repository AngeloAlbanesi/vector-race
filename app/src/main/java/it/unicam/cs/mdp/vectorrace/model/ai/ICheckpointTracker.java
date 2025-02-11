package it.unicam.cs.mdp.vectorrace.model.ai;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Interfaccia per il tracciamento dei checkpoint attraversati dai giocatori.
 */
public interface ICheckpointTracker {
    /**
     * Verifica se un giocatore ha attraversato un determinato checkpoint.
     * 
     * @param playerName nome del giocatore
     * @param checkpoint posizione del checkpoint
     * @return true se il checkpoint è stato attraversato
     */
    boolean hasPassedCheckpoint(String playerName, Position checkpoint);

    /**
     * Registra un checkpoint come attraversato per un giocatore.
     * 
     * @param playerName nome del giocatore
     * @param checkpoint posizione del checkpoint
     */
    void markCheckpointAsPassed(String playerName, Position checkpoint);
}