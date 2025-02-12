package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;

import java.util.Set;

/**
 * Interfaccia per il rendering grafico dello stato del gioco.
 * Si occupa della rappresentazione visiva dello stato del gioco usando JavaFX.
 */
public interface IGUIRenderer {
    /**
     * Renderizza graficamente lo stato del gioco.
     * 
     * @param state lo stato del gioco da renderizzare
     * @param validMoves le posizioni valide per il movimento corrente
     */
    void render(GameState state, Set<Position> validMoves);
}