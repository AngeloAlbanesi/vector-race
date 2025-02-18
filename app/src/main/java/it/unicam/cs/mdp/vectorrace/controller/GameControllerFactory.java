package it.unicam.cs.mdp.vectorrace.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.TrackLoader;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.model.players.PlayerFactory;
import it.unicam.cs.mdp.vectorrace.view.GameView;

/**
 * Factory per la creazione di istanze di IGameController.
 * Questa classe implementa il pattern Factory per disaccoppiare la creazione
 * dei controller dalla loro interfaccia.
 */
public class GameControllerFactory {
    
    /**
     * Crea una nuova istanza di IGameController.
     *
     * @param trackFile Il percorso del file del circuito
     * @param playerFile Il percorso del file dei giocatori
     * @param view L'interfaccia di visualizzazione
     * @return Una nuova istanza di IGameController
     * @throws IOException Se si verificano errori durante il caricamento dei file
     */
    public IGameController createController(String trackFile, String playerFile, GameView view) throws IOException {
        GameState gameState = initializeGame(trackFile, playerFile);
        return new GameController(gameState, view);
    }

    /**
     * Inizializza un nuovo stato di gioco.
     *
     * @param trackFile Il percorso del file del circuito
     * @param playerFile Il percorso del file dei giocatori
     * @return Il nuovo stato di gioco inizializzato
     * @throws IOException Se si verificano errori durante il caricamento dei file
     */
    private GameState initializeGame(String trackFile, String playerFile) throws IOException {
        logInitializationStart();
        Track track = loadTrack(trackFile);
        List<Position> startPositions = validateStartPositions(track);
        List<Player> players = initializePlayers(playerFile, startPositions);
        return createGameState(track, players);
    }

    private void logInitializationStart() {
        System.out.println("Inizializzazione gioco...");
    }

    private Track loadTrack(String trackFile) throws IOException {
        System.out.println("Caricamento circuito da: " + trackFile);
        Track track = TrackLoader.loadTrack(trackFile);
        System.out.println("Circuito caricato: " + track.getWidth() + "x" + track.getHeight());
        return track;
    }

    private List<Position> validateStartPositions(Track track) {
        List<Position> startPositions = findStartPositions(track);
        System.out.println("Trovate " + startPositions.size() + " posizioni di partenza");
        
        if (startPositions.isEmpty()) {
            throw new IllegalStateException("Il circuito non ha posizioni di partenza (S)");
        }
        
        return startPositions;
    }

    private List<Player> initializePlayers(String playerFile, List<Position> startPositions) throws IOException {
        System.out.println("Caricamento giocatori da: " + playerFile);
        List<Player> players = PlayerFactory.createPlayersFromFile(playerFile, startPositions);
        System.out.println("Creati " + players.size() + " giocatori");
        return players;
    }

    private GameState createGameState(Track track, List<Player> players) {
        return new GameState(track, players);
    }

    private List<Position> findStartPositions(Track track) {
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
}