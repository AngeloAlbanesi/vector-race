package it.unicam.cs.mdp.vectorrace.model;

/**
 * Interfaccia per le strategie AI.
 */
public interface AIStrategy {

    /**
     * Calcola la prossima accelerazione per il giocatore.
     *
     * @param player giocatore per cui calcolare la mossa
     * @param gameState stato corrente del gioco
     * @return vettore accelerazione scelto
     */
    Vector getNextAcceleration(Player player, GameState gameState);
}