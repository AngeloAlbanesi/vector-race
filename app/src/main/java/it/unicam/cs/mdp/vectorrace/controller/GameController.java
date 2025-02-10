package it.unicam.cs.mdp.vectorrace.controller;

import it.unicam.cs.mdp.vectorrace.model.*;
import it.unicam.cs.mdp.vectorrace.view.*;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller principale che inizializza e gestisce il ciclo di gioco.
 */
public class GameController {

    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final GameState gameState;
    private final CLIView cliView;

    private void logDebug(String message) {
        String timestamp = LocalDateTime.now().format(LOG_FORMATTER);
        System.out.printf("[DEBUG %s] %s%n", timestamp, message);
    }

    private void logState() {
        logDebug("=== STATO DEL GIOCO ===");
        for (Player p : gameState.getPlayers()) {
            logDebug(String.format("Giocatore: %s", p.getName()));
            logDebug(String.format("  Posizione: %s", p.getPosition()));
            logDebug(String.format("  Velocità: %s", p.getVelocity()));
            logDebug(String.format("  Checkpoint raggiunti: %d", p.getNextCheckpointIndex()));
        }
        logDebug("====================");
    }

    /**
     * Costruttore.
     *
     * @param gameState stato iniziale della gara
     * @param cliView   interfaccia a riga di comando
     */
    public GameController(GameState gameState, CLIView cliView) {
        this.gameState = gameState;
        this.cliView = cliView;
    }

    /**
     * Avvia il ciclo di gioco fino a quando non viene determinato un vincitore.
     */
    public void startGame() {

    }

    /**
     * Carica i giocatori da un file di configurazione. Formato (una riga per
     * giocatore): Tipo;Nome;Colore (HEX);Strategia (1 = simplestrategy, 2 =
     * BFS)
     *
     * @param filePath       percorso del file dei giocatori
     * @param startPositions lista di posizioni di partenza ottenute dal
     *                       circuito
     * @return lista di giocatori creati
     * @throws IOException se si verifica un errore di I/O
     */
    public static List<Player> loadPlayers(String filePath, List<Position> startPositions) throws IOException {
        System.out.printf("[DEBUG] Caricamento giocatori da: %s%n", filePath);
        System.out.printf("[DEBUG] Posizioni di partenza disponibili: %d%n", startPositions.size());

        List<Player> players = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int startIndex = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length < 3) {
                    continue;
                }
                String type = parts[0].trim();
                String name = parts[1].trim();
                Color color = Color.decode("#" + parts[2].trim());
                if (startIndex >= startPositions.size()) {
                    throw new IllegalArgumentException(
                            "Non ci sono abbastanza posizioni di partenza per tutti i giocatori.");
                }
                Position startPos = startPositions.get(startIndex++);
                if (type.equalsIgnoreCase("Human")) {
                    players.add(new HumanPlayer(name, color, startPos));
                } else if (type.equalsIgnoreCase("Bot")) {
                    int strategyType = 1; // Default to SimpleStrategy
                    if (parts.length >= 4) {
                        try {
                            strategyType = Integer.parseInt(parts[3].trim());
                        } catch (NumberFormatException e) {
                            // Usa default
                        }
                    }
                    AIStrategy strategy;
                    switch (strategyType) {
                        case 1:
                            strategy = new BFSStrategy();
                            break;
                        case 2:
                            strategy = new PureAStarStrategy();
                            break;
                        default:
                            strategy = new BFSStrategy();
                            break;
                    }
                    players.add(new BotPlayer(name, color, startPos, strategy));
                }
            }
        }
        return players;
    }

    /**
     * Inizializza il gioco caricando il circuito e i giocatori.
     *
     * @param trackFile  percorso del file del circuito
     * @param playerFile percorso del file dei giocatori
     * @return stato iniziale della gara
     * @throws IOException in caso di errori di I/O
     */
    public static GameState initializeGame(String trackFile, String playerFile) throws IOException {
        System.out.printf("[DEBUG] Inizializzazione gioco%n");
        System.out.printf("[DEBUG] Caricamento circuito: %s%n", trackFile);
        Track track = TrackLoader.loadTrack(trackFile);
        System.out.printf("[DEBUG] Dimensioni circuito: %dx%d%n", track.getWidth(), track.getHeight());

        List<Position> startPositions = TrackLoader.getStartPositions(track);
        System.out.printf("[DEBUG] Posizioni di partenza trovate: %d%n", startPositions.size());
        if (startPositions.isEmpty()) {
            throw new IllegalArgumentException("Nessuna posizione di partenza trovata nel circuito.");
        }
        List<Player> players = loadPlayers(playerFile, startPositions);
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Nessun giocatore caricato.");
        }
        if (players.size() > startPositions.size()) {
            throw new IllegalArgumentException("Troppi giocatori per le posizioni di partenza disponibili.");
        }
        return new GameState(track, players);
    }
}