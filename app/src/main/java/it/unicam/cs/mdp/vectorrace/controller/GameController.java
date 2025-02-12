package it.unicam.cs.mdp.vectorrace.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.TrackLoader;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.game.TurnManager;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.model.players.PlayerFactory;
import it.unicam.cs.mdp.vectorrace.view.GameView;

/**
 * Controller principale del gioco che coordina le varie componenti.
 * Implementa il pattern Mediator per la comunicazione tra i componenti.
 */
public class GameController {
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
     * Inizializza un nuovo gioco.
     * 
     * @param trackFile Il percorso del file del circuito
     * @param playerFile Il percorso del file dei giocatori
     * @return Il nuovo stato di gioco inizializzato
     * @throws IOException Se si verificano errori durante il caricamento dei file
     */
    public static GameState initializeGame(String trackFile, String playerFile) throws IOException {
        // Carica il circuito
        System.out.println("Inizializzazione gioco...");
        System.out.println("Caricamento circuito da: " + trackFile);
        Track track = TrackLoader.loadTrack(trackFile);
        System.out.println("Circuito caricato: " + track.getWidth() + "x" + track.getHeight());

        // Trova le posizioni di partenza
        List<Position> startPositions = findStartPositions(track);
        System.out.println("Trovate " + startPositions.size() + " posizioni di partenza");
        if (startPositions.isEmpty()) {
            throw new IllegalStateException("Il circuito non ha posizioni di partenza (S)");
        }

        // Carica i giocatori
        System.out.println("Caricamento giocatori da: " + playerFile);
        List<Player> players = PlayerFactory.createPlayersFromFile(playerFile, startPositions);
        System.out.println("Creati " + players.size() + " giocatori");

        return new GameState(track, players);
    }

    /**
     * Trova tutte le posizioni di partenza nel circuito.
     * 
     * @param track Il circuito da analizzare
     * @return Lista delle posizioni di partenza trovate
     */
    private static List<Position> findStartPositions(Track track) {
        List<Position> startPositions = new ArrayList<>();
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.isStart(x, y)) {
                    startPositions.add(new Position(x, y));
                }
            }
        }
        return startPositions;
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
