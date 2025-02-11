package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp.vectorrace.model.ai.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.ai.BFSStrategy;
import it.unicam.cs.mdp.vectorrace.model.ai.PureAStarStrategy;
import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Factory class responsabile della creazione dei giocatori.
 * Implementa il Factory Pattern per incapsulare la logica di creazione.
 */
public class PlayerFactory {
    
    /**
     * Crea una lista di giocatori dal file specificato.
     *
     * @param playerFile Il percorso del file dei giocatori
     * @param startPositions Le posizioni di partenza disponibili
     * @return Lista dei giocatori creati
     * @throws IOException Se si verificano errori durante la lettura del file
     * @throws IllegalStateException Se non ci sono abbastanza posizioni di partenza
     */
    public static List<Player> createPlayersFromFile(String playerFile, List<Position> startPositions) throws IOException {
        List<Player> players = new ArrayList<>();
        int startPosIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(playerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    if (startPosIndex >= startPositions.size()) {
                        throw new IllegalStateException(
                                "Non ci sono abbastanza posizioni di partenza per tutti i giocatori");
                    }

                    Player player = createPlayer(parts, startPositions.get(startPosIndex++));
                    if (player != null) {
                        players.add(player);
                    }
                } else {
                    System.err.println("Linea invalida nel file giocatori: " + line);
                }
            }
        }

        if (players.isEmpty()) {
            throw new IllegalStateException("Nessun giocatore valido trovato nel file " + playerFile);
        }

        return players;
    }

    /**
     * Crea un singolo giocatore dai dati forniti.
     *
     * @param playerData I dati del giocatore dal file
     * @param startPos La posizione di partenza
     * @return Il giocatore creato o null se i dati non sono validi
     */
    private static Player createPlayer(String[] playerData, Position startPos) {
        try {
            String tipo = playerData[0].trim();
            String name = playerData[1].trim();
            String colorHex = playerData[2].trim();
            Color color = Color.decode(colorHex);

            if (tipo.equalsIgnoreCase("Human")) {
                return createHumanPlayer(name, color, startPos);
            } else if (tipo.equalsIgnoreCase("Bot")) {
                return createBotPlayer(name, color, startPos, playerData);
            } else {
                System.err.println("Tipo giocatore non valido: " + tipo);
                return null;
            }
        } catch (NumberFormatException e) {
            System.err.println("Errore nel parsing dei dati per il giocatore: " + e.getMessage());
            return null;
        }
    }

    /**
     * Crea un giocatore umano.
     */
    private static Player createHumanPlayer(String name, Color color, Position startPos) {
        return new HumanPlayer(name, color, startPos);
    }

    /**
     * Crea un giocatore bot con la strategia specificata.
     */
    private static Player createBotPlayer(String name, Color color, Position startPos, String[] playerData) {
        AIStrategy strategy;
        if (playerData.length > 3) {
            int strategia = Integer.parseInt(playerData[3].trim());
            if (strategia == 2) {
                strategy = new PureAStarStrategy();
            } else {
                strategy = new BFSStrategy();
            }
        } else {
            System.err.println("Strategia non specificata per il bot " + name + ", uso BFS di default");
            strategy = new BFSStrategy();
        }
        return new BotPlayer(name, color, startPos, strategy);
    }
}