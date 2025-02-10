// HumanPlayer.java
package it.unicam.cs.mdp.vectorrace.model;

import java.awt.Color;

/**
 * Rappresenta un giocatore controllato da un utente umano. L’input viene
 * fornito tramite interfaccia (CLI o GUI).
 */
public class HumanPlayer extends Player {

    /**
     * Costruttore.
     *
     * @param name nome del giocatore
     * @param color colore per visualizzazione
     * @param startPosition posizione iniziale
     */
    public HumanPlayer(String name, Color color, Position startPosition) {
        super(name, color, startPosition);
    }

    /**
     * Per i giocatori umani, l’input verrà gestito tramite interfaccia; qui
     * viene restituita una accelerazione di default (0,0).
     */
    @Override
    public Vector getNextAcceleration(GameState gameState) {
        // L'input reale sarà gestito dalla GUI/CLI; qui si restituisce un dummy.
        return new Vector(0, 0);
    }
}