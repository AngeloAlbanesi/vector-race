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
 * A factory class responsible for creating instances of {@link IGameController}.
 * This class implements the Factory pattern to decouple controller creation from its implementation.
 * It handles the complete initialization process including loading tracks, creating players,
 * and setting up the game state.
 *
 * <p>The factory manages:
 * <ul>
 *   <li>Track loading and validation</li>
 *   <li>Player initialization from configuration files</li>
 *   <li>Game state setup</li>
 *   <li>Controller instantiation</li>
 * </ul>
 */
public class GameControllerFactory {
    
    /**
     * Creates a new instance of {@link IGameController} with the specified configuration.
     * This method orchestrates the complete initialization process of the game.
     *
     * @param trackFile  The path to the track configuration file.
     * @param playerFile The path to the player configuration file.
     * @param view       The game view interface for rendering the game.
     * @return A fully initialized game controller instance.
     * @throws IOException If there are errors reading the track or player configuration files.
     */
    public IGameController createController(String trackFile, String playerFile, GameView view) throws IOException {
        GameState gameState = initializeGame(trackFile, playerFile);
        return new GameController(gameState, view);
    }

    /**
     * Initializes a new game state by loading and validating all required components.
     * This method coordinates the loading of track and player data, ensuring all
     * game elements are properly initialized.
     *
     * @param trackFile  The path to the track configuration file.
     * @param playerFile The path to the player configuration file.
     * @return A fully initialized game state.
     * @throws IOException If there are errors reading the configuration files.
     */
    private GameState initializeGame(String trackFile, String playerFile) throws IOException {
        logInitializationStart();
        Track track = loadTrack(trackFile);
        List<Position> startPositions = validateStartPositions(track);
        List<Player> players = initializePlayers(playerFile, startPositions);
        return createGameState(track, players);
    }

    /**
     * Logs the start of the game initialization process.
     */
    private void logInitializationStart() {
        System.out.println("Inizializzazione gioco...");
    }

    /**
     * Loads and validates the track from the specified file.
     *
     * @param trackFile The path to the track configuration file.
     * @return The loaded track instance.
     * @throws IOException If there are errors reading the track file.
     */
    private Track loadTrack(String trackFile) throws IOException {
        System.out.println("Caricamento circuito da: " + trackFile);
        Track track = TrackLoader.loadTrack(trackFile);
        System.out.println("Circuito caricato: " + track.getWidth() + "x" + track.getHeight());
        return track;
    }

    /**
     * Validates and collects all starting positions from the track.
     * Ensures the track has at least one valid starting position.
     *
     * @param track The track to validate.
     * @return A list of valid starting positions.
     * @throws IllegalStateException If no starting positions are found.
     */
    private List<Position> validateStartPositions(Track track) {
        List<Position> startPositions = findStartPositions(track);
        System.out.println("Trovate " + startPositions.size() + " posizioni di partenza");
        
        if (startPositions.isEmpty()) {
            throw new IllegalStateException("Il circuito non ha posizioni di partenza (S)");
        }
        
        return startPositions;
    }

    /**
     * Initializes players from the configuration file and assigns starting positions.
     *
     * @param playerFile     The path to the player configuration file.
     * @param startPositions The list of valid starting positions.
     * @return A list of initialized players.
     * @throws IOException If there are errors reading the player configuration file.
     */
    private List<Player> initializePlayers(String playerFile, List<Position> startPositions) throws IOException {
        System.out.println("Caricamento giocatori da: " + playerFile);
        List<Player> players = PlayerFactory.createPlayersFromFile(playerFile, startPositions);
        System.out.println("Creati " + players.size() + " giocatori");
        return players;
    }

    /**
     * Creates a new game state with the specified track and players.
     *
     * @param track   The initialized track.
     * @param players The list of initialized players.
     * @return A new game state instance.
     */
    private GameState createGameState(Track track, List<Player> players) {
        return new GameState(track, players);
    }

    /**
     * Finds all starting positions on the track.
     * Scans the entire track grid to locate cells marked as starting positions.
     *
     * @param track The track to scan.
     * @return A list of starting positions.
     */
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