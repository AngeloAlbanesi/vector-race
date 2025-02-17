package it.unicam.cs.mdp.vectorrace.model.players;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AStar.PureAStarStrategy;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.bfs.BFSStrategy;
import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Factory class responsabile della creazione dei giocatori.
 * Implementa il Factory Pattern per incapsulare la logica di creazione.
 */
public class PlayerFactory {
    private static final PlayerParser parser = new PlayerParser();

    /**
     * Crea una lista di giocatori dal file specificato.
     *
     * @param playerFile     Il percorso del file dei giocatori
     * @param startPositions Le posizioni di partenza disponibili
     * @return Lista dei giocatori creati
     * @throws IOException Se si verificano errori durante la lettura del file
     * @throws PlayerParsingException Se ci sono errori nel parsing dei dati dei giocatori
     */
    public static List<Player> createPlayersFromFile(String playerFile, List<Position> startPositions)
            throws IOException {
        List<String[]> playerDataList = parser.parsePlayerFile(playerFile);
        return createPlayersFromData(playerDataList, startPositions);
    }

    /**
     * Crea una lista di giocatori dai dati parsati.
     */
    private static List<Player> createPlayersFromData(List<String[]> playerDataList, List<Position> startPositions) {
        List<Player> players = new ArrayList<>();
        int startPosIndex = 0;

        for (String[] rawPlayerData : playerDataList) {
            checkStartPosition(startPosIndex, startPositions.size());
            Position startPos = startPositions.get(startPosIndex++);
            
            PlayerParser.PlayerData playerData = parser.validatePlayerData(rawPlayerData);
            Player player = createPlayerFromValidatedData(playerData, startPos);
            players.add(player);
        }

        return players;
    }

    /**
     * Verifica la disponibilità delle posizioni di partenza.
     */
    private static void checkStartPosition(int index, int totalPositions) {
        if (index >= totalPositions) {
            throw new IllegalStateException(
                "Non ci sono abbastanza posizioni di partenza per tutti i giocatori");
        }
    }

    /**
     * Crea un giocatore dai dati validati.
     */
    private static Player createPlayerFromValidatedData(PlayerParser.PlayerData data, Position startPos) {
        return switch (data.getType()) {
            case "human" -> createHumanPlayer(data, startPos);
            case "bot" -> createBotPlayer(data, startPos);
            default -> throw new PlayerParsingException.InvalidPlayerTypeException(data.getType());
        };
    }

    /**
     * Crea un giocatore umano.
     */
    private static Player createHumanPlayer(PlayerParser.PlayerData data, Position startPos) {
        return new HumanPlayer(data.getName(), data.getColor(), startPos);
    }

    /**
     * Crea un giocatore bot con la strategia specificata.
     */
    private static Player createBotPlayer(PlayerParser.PlayerData data, Position startPos) {
        AIStrategy strategy = createStrategy(data.getStrategy());
        return new BotPlayer(data.getName(), data.getColor(), startPos, strategy);
    }

    /**
     * Crea la strategia AI appropriata basata sul tipo.
     */
    private static AIStrategy createStrategy(StrategyType strategyType) {
        if (strategyType == null) {
            return new BFSStrategy();
        }
        
        return switch (strategyType) {
            case ASTAR -> new PureAStarStrategy();
            case BFS -> new BFSStrategy();
        };
    }
}