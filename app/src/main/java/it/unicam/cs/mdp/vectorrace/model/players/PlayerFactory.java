package it.unicam.cs.mdp.vectorrace.model.players;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.AStar.PureAStarStrategy;
import it.unicam.cs.mdp.vectorrace.model.ai.strategies.bfs.BFSStrategy;
import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Factory class responsible for creating player instances in the Vector Race game.
 * Implements the Factory pattern to encapsulate player creation logic and manage
 * the complexity of instantiating different player types with their configurations.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Player creation from configuration files</li>
 *   <li>Player type validation and instantiation</li>
 *   <li>Starting position management</li>
 *   <li>AI strategy creation and assignment</li>
 * </ul>
 * 
 * <p>Supported player types:
 * <ul>
 *   <li>Human players with manual control</li>
 *   <li>Bot players with AI strategies (BFS or A*)</li>
 * </ul>
 */
public class PlayerFactory {
    private static final PlayerParser parser = new PlayerParser();

    /**
     * Creates a list of players from a configuration file.
     * This method handles the complete player creation process including:
     * <ul>
     *   <li>File parsing and data validation</li>
     *   <li>Starting position assignment</li>
     *   <li>Player type-specific initialization</li>
     * </ul>
     *
     * @param playerFile The path to the player configuration file.
     * @param startPositions List of available starting positions on the track.
     * @return List of initialized players ready for the game.
     * @throws IOException If there are errors reading the configuration file.
     * @throws PlayerParsingException If player data is invalid or malformed.
     */
    public static List<Player> createPlayersFromFile(String playerFile, List<Position> startPositions)
            throws IOException {
        List<String[]> playerDataList = parser.parsePlayerFile(playerFile);
        return createPlayersFromData(playerDataList, startPositions);
    }

    /**
     * Creates players from parsed configuration data.
     * Validates and processes each player's configuration, assigning starting
     * positions and creating appropriate player instances.
     *
     * @param playerDataList List of raw player configuration data.
     * @param startPositions Available starting positions.
     * @return List of initialized players.
     * @throws IllegalStateException If there aren't enough starting positions.
     */
    private static List<Player> createPlayersFromData(List<String[]> playerDataList, List<Position> startPositions) {
        List<Player> players = new ArrayList<>();
        int startPosIndex = 0;

        for (String[] rawPlayerData : playerDataList) {
            checkStartPosition(startPosIndex, startPositions.size());
            Position startPos = startPositions.get(startPosIndex++);
            
            PlayerData playerData = parser.validatePlayerData(rawPlayerData);
            Player player = createPlayerFromValidatedData(playerData, startPos);
            players.add(player);
        }

        return players;
    }

    /**
     * Verifies availability of starting positions.
     * Ensures there are enough starting positions for all players.
     *
     * @param index Current starting position index.
     * @param totalPositions Total number of available positions.
     * @throws IllegalStateException If there aren't enough positions.
     */
    private static void checkStartPosition(int index, int totalPositions) {
        if (index >= totalPositions) {
            throw new IllegalStateException(
                "Non ci sono abbastanza posizioni di partenza per tutti i giocatori");
        }
    }

    /**
     * Creates a player instance from validated configuration data.
     * Delegates to specific creation methods based on player type.
     *
     * @param data Validated player configuration data.
     * @param startPos Starting position for the player.
     * @return Initialized player instance.
     * @throws PlayerParsingException.InvalidPlayerTypeException If player type is invalid.
     */
    private static Player createPlayerFromValidatedData(PlayerData data, Position startPos) {
        return switch (data.getType()) {
            case "human" -> createHumanPlayer(data, startPos);
            case "bot" -> createBotPlayer(data, startPos);
            default -> throw new PlayerParsingException.InvalidPlayerTypeException(data.getType());
        };
    }

    /**
     * Creates a human player instance.
     * Initializes a player with manual control capabilities.
     *
     * @param data Player configuration data.
     * @param startPos Starting position.
     * @return Initialized human player.
     */
    private static Player createHumanPlayer(PlayerData data, Position startPos) {
        return new HumanPlayer(data.getName(), data.getColor(), startPos);
    }

    /**
     * Creates a bot player instance with AI strategy.
     * Initializes an AI-controlled player with the specified strategy.
     *
     * @param data Player configuration data including strategy type.
     * @param startPos Starting position.
     * @return Initialized bot player.
     */
    private static Player createBotPlayer(PlayerData data, Position startPos) {
        AIStrategy strategy = createStrategy(data.getStrategy());
        return new BotPlayer(data.getName(), data.getColor(), startPos, strategy);
    }

    /**
     * Creates an AI strategy instance based on the specified type.
     * Defaults to BFS strategy if no type is specified.
     *
     * @param strategyType The type of AI strategy to create.
     * @return Initialized AI strategy instance.
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