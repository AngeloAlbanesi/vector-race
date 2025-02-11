package it.unicam.cs.mdp.vectorrace.controller;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.view.CLIView;
import it.unicam.cs.mdp.vectorrace.model.ai.BFSStrategy;
import it.unicam.cs.mdp.vectorrace.model.ai.PriorityData;
import it.unicam.cs.mdp.vectorrace.model.ai.PureAStarStrategy;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.BotPlayer;
import it.unicam.cs.mdp.vectorrace.model.players.HumanPlayer;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Controller principale che inizializza e gestisce il ciclo di gioco.
 */
public class GameController {
    private final GameState gameState;
    private final CLIView cliView;
    private final MovementManager movementManager;

    public GameController(GameState gameState, CLIView cliView) {
        this.gameState = gameState;
        this.cliView = cliView;
        this.movementManager = new MovementManager();
    }

    private static Track loadTrack(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                lines.add(line);
            }
        }

        if (lines.isEmpty()) {
            throw new IOException("Il file del circuito è vuoto");
        }

        int height = lines.size();
        int width = lines.get(0).length();
        CellType[][] grid = new CellType[height][width];
        Map<Position, PriorityData> checkpointData = new HashMap<>();

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if (Character.isDigit(c)) {
                    grid[y][x] = CellType.CHECKPOINT;
                    int checkpointNumber = Character.getNumericValue(c);
                    checkpointData.put(
                            new Position(x, y),
                            new PriorityData(calculatePriorityLevel(checkpointNumber), checkpointNumber));
                } else {
                    grid[y][x] = CellType.fromChar(c);
                }
            }
        }

        return new Track(grid, checkpointData);
    }

    private static int calculatePriorityLevel(int checkpointNumber) {
        if (checkpointNumber <= 3) {
            return 1; // Priorità bassa
        } else if (checkpointNumber <= 6) {
            return 2; // Priorità media
        } else {
            return 3; // Priorità alta
        }
    }

    public static GameState initializeGame(String trackFile, String playerFile) throws IOException {
        System.out.println("Inizializzazione gioco...");
        System.out.println("Caricamento circuito da: " + trackFile);
        Track track = loadTrack(trackFile);
        System.out.println("Circuito caricato: " + track.getWidth() + "x" + track.getHeight());

        List<Position> startPositions = new ArrayList<>();
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.isStart(x, y)) {
                    startPositions.add(new Position(x, y));
                }
            }
        }

        System.out.println("Trovate " + startPositions.size() + " posizioni di partenza");
        if (startPositions.isEmpty()) {
            throw new IllegalStateException("Il circuito non ha posizioni di partenza (S)");
        }

        System.out.println("Caricamento giocatori da: " + playerFile);
        List<Player> players = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(playerFile))) {
            String line;
            int startPosIndex = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Salta linee vuote o commenti
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length >= 3) { // Cambiato da 4 a 3 per supportare i giocatori umani
                    String tipo = parts[0].trim();
                    String name = parts[1].trim();
                    String colorHex = parts[2].trim();

                    try {
                        // Converti il colore da hex a RGB
                        Color color = Color.decode(colorHex);

                        if (startPosIndex >= startPositions.size()) {
                            throw new IllegalStateException(
                                    "Non ci sono abbastanza posizioni di partenza per tutti i giocatori");
                        }

                        Position startPos = startPositions.get(startPosIndex++);
                        System.out.println("Creazione giocatore: " + name + " in posizione " + startPos);

                        // Crea il giocatore in base al tipo
                        Player player;
                        if (tipo.equalsIgnoreCase("Human")) {
                            player = new HumanPlayer(name, color, startPos);
                        } else if (tipo.equalsIgnoreCase("Bot")) {
                            int strategia = Integer.parseInt(parts[3].trim());
                            if (strategia == 1) {
                                player = new BotPlayer(name, color, startPos, new BFSStrategy());
                            } else if (strategia == 2) {
                                player = new BotPlayer(name, color, startPos, new PureAStarStrategy());
                            } else {
                                System.err.println(
                                        "Strategia non valida per il giocatore " + name + ", uso BFS di default");
                                player = new BotPlayer(name, color, startPos, new BFSStrategy());
                            }
                        } else {
                            System.err.println("Tipo giocatore non valido: " + tipo + ", ignoro la riga");
                            continue;
                        }
                        players.add(player);

                    } catch (NumberFormatException e) {
                        System.err.println(
                                "Errore nel parsing dei dati per il giocatore " + name + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Linea invalida nel file giocatori: " + line);
                }
            }
        }

        if (players.isEmpty()) {
            throw new IllegalStateException("Nessun giocatore valido trovato nel file " + playerFile);
        }

        System.out.println("Creati " + players.size() + " giocatori");
        return new GameState(track, players);
    }

    public void advanceTurn() {
        Player currentPlayer = gameState.getCurrentPlayer();
        Track track = gameState.getTrack();

        // Ottieni l'accelerazione dal giocatore (bot)
        Vector acceleration = currentPlayer.getNextAcceleration(gameState);
        if (acceleration == null) {
            cliView.displayMessage(currentPlayer.getName() + " non ha fornito un'accelerazione valida.");
            acceleration = new Vector(0, 0);
        }

        // Verifica movimento
        if (!movementManager.validateMove(currentPlayer, acceleration, gameState)) {
            cliView.displayMessage(
                    currentPlayer.getName() + " ha colliso con un muro o giocatore fermo! Velocità resettata.");
            currentPlayer.resetVelocity();
        } else {
            // Movimento valido
            Vector newVelocity = currentPlayer.getVelocity().add(acceleration);
            Position newPosition = currentPlayer.getPosition().move(newVelocity);

            // Verifica sovrapposizione con altri giocatori
            boolean occupied = false;
            for (Player other : gameState.getPlayers()) {
                if (other != currentPlayer && other.getPosition().equals(newPosition)) {
                    occupied = true;
                    break;
                }
            }

            if (occupied) {
                cliView.displayMessage(
                        currentPlayer.getName() + " ha trovato la cella occupata da un altro giocatore, resta fermo!");
                currentPlayer.resetVelocity();
            } else {
                currentPlayer.updatePosition(newPosition);
                currentPlayer.updateVelocity(newVelocity);

                // Verifica se ha raggiunto il traguardo
                if (track.getCell(newPosition.getX(), newPosition.getY()) == CellType.FINISH) {
                    gameState.setFinished(true);
                    gameState.setWinner(currentPlayer);
                    cliView.displayMessage("Il Giocatore " + currentPlayer.getName() + " ha vinto la gara!");
                    cliView.displayGameState(gameState);
                    return;
                }
            }
        }

        // Controlla fine gara
        if (gameState.checkFinish(currentPlayer)) {
            gameState.setFinished(true);
            gameState.setWinner(currentPlayer);
            cliView.displayMessage("Il Giocatore " + currentPlayer.getName() + " ha vinto la gara!");
            cliView.displayGameState(gameState);
            return;
        }

        // Passa al prossimo turno
        gameState.nextTurn();

        // Aggiorna la vista
        cliView.displayGameState(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }
}
