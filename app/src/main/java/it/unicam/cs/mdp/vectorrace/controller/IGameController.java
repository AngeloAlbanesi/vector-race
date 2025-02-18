package it.unicam.cs.mdp.vectorrace.controller;

import it.unicam.cs.mdp.vectorrace.model.game.GameState;

/**
 * Interfaccia che definisce il contratto per il controller principale del gioco.
 * Implementa il pattern Mediator per la comunicazione tra i componenti.
 * Questa interfaccia supporta l'inversione delle dipendenze (DIP) e facilita il testing.
 */
public interface IGameController {
    
    /**
     * Restituisce lo stato corrente del gioco.
     *
     * @return Lo stato corrente del gioco
     */
    GameState getGameState();

    /**
     * Avanza al prossimo turno di gioco.
     */
    void advanceTurn();
}