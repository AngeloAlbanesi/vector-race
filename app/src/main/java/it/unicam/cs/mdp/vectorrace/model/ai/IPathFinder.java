package it.unicam.cs.mdp.vectorrace.model.ai;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Interfaccia che definisce il contratto per la ricerca del percorso
 * nell'implementazione di una strategia AI.
 */
public interface IPathFinder {
    /**
     * Trova il prossimo vettore di accelerazione per raggiungere il target.
     *
     * @param player il giocatore corrente
     * @param gameState lo stato del gioco
     * @param target la posizione target da raggiungere
     * @return il vettore di accelerazione da applicare
     */
    Vector findPath(Player player, GameState gameState, Position target);
}