package it.unicam.cs.mdp.vectorrace.controller;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.game.TurnManager;
import it.unicam.cs.mdp.vectorrace.view.GameView;

/**
 * Controller principale del gioco che coordina le varie componenti.
 * Implementa il pattern Mediator per la comunicazione tra i componenti.
 */
public class GameController implements IGameController {
    private final GameState gameState;
    private final GameView view;
    private final TurnManager turnManager;

    /**
     * Costruisce un nuovo controller di gioco.
     *
     * @param gameState Lo stato del gioco
     * @param view L'interfaccia di visualizzazione
     */
    public GameController(GameState gameState, GameView view) {
        this.gameState = gameState;
        this.view = view;
        this.turnManager = new TurnManager(gameState, new MovementManager(), view);
    }

    /**
     * Avanza al prossimo turno di gioco.
     */
    public void advanceTurn() {
        turnManager.advanceTurn();
    }

    /**
     * Restituisce lo stato corrente del gioco.
     */
    public GameState getGameState() {
        return gameState;
    }
}
