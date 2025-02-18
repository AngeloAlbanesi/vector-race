package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;

import java.util.Set;

/**
 * Interface for the graphical rendering of the game state.
 * It is responsible for the visual representation of the game state using JavaFX.
 */
public interface IGUIRenderer {
    /**
     * Renders the game state graphically.
     *
     * @param state      The state of the game to render.
     * @param validMoves The valid positions for the current move.
     */
    void render(GameState state, Set<Position> validMoves);
}