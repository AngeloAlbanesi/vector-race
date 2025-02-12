package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.model.core.CellType;

/**
 * Implementazione CLI del renderer del gioco.
 * Responsabile della conversione dello stato del gioco in una rappresentazione testuale.
 */
public class CLIGameRenderer implements IGameRenderer {
    private final IPlayerInfoFormatter playerInfoFormatter;

    public CLIGameRenderer(IPlayerInfoFormatter playerInfoFormatter) {
        this.playerInfoFormatter = playerInfoFormatter;
    }

    @Override
    public String renderGameState(GameState gameState) {
        StringBuilder output = new StringBuilder();
        
        // Rendering della griglia
        String[][] grid = buildGrid(gameState);
        output.append("=== Stato attuale del circuito ===\n");
        output.append(renderGrid(grid));
        
        // Aggiunge le informazioni dei giocatori
        output.append(playerInfoFormatter.formatPlayersInfo(gameState.getPlayers()));
        
        // Gestisce lo stato di fine gioco
        if (gameState.isFinished() && gameState.getWinner() != null) {
            output.append(String.format("\n🏆 Il Giocatore %s ha vinto la gara! 🏆\n", 
                gameState.getWinner().getName()));
        }
        
        return output.toString();
    }

    /**
     * Costruisce la griglia di gioco con i simboli appropriati.
     */
    private String[][] buildGrid(GameState gameState) {
        Track track = gameState.getTrack();
        int width = track.getWidth();
        int height = track.getHeight();
        String[][] grid = new String[height][width];

        // Inizializza la griglia con i tipi di cella
        initializeGrid(grid, track);
        
        // Aggiunge i giocatori alla griglia
        addPlayersToGrid(grid, gameState);

        return grid;
    }

    /**
     * Inizializza la griglia con i simboli base per ogni tipo di cella.
     */
    private void initializeGrid(String[][] grid, Track track) {
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                CellType cell = track.getCell(x, y);
                grid[y][x] = getCellSymbol(cell);
            }
        }
    }

    /**
     * Restituisce il simbolo appropriato per ogni tipo di cella.
     */
    private String getCellSymbol(CellType cellType) {
        return switch (cellType) {
            case WALL -> "#";
            case ROAD, CHECKPOINT -> ".";
            case START -> "S";
            case FINISH -> "*";
        };
    }

    /**
     * Aggiunge i giocatori alla griglia.
     */
    private void addPlayersToGrid(String[][] grid, GameState gameState) {
        for (Player player : gameState.getPlayers()) {
            Position pos = player.getPosition();
            if (gameState.getTrack().isWithinBounds(pos.getX(), pos.getY())) {
                String playerNumber = player.getName().replaceAll("\\D+", "");
                grid[pos.getY()][pos.getX()] = playerNumber;
            }
        }
    }

    /**
     * Converte la griglia in una stringa formattata.
     */
    private String renderGrid(String[][] grid) {
        StringBuilder result = new StringBuilder();
        for (String[] row : grid) {
            for (String cell : row) {
                result.append(cell);
            }
            result.append('\n');
        }
        return result.toString();
    }
}