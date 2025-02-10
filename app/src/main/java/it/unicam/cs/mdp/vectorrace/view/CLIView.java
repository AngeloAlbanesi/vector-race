package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.GameState;
import it.unicam.cs.mdp.vectorrace.model.Player;
import it.unicam.cs.mdp.vectorrace.model.Position;
import it.unicam.cs.mdp.vectorrace.model.Track;
import it.unicam.cs.mdp.vectorrace.model.CellType;

/**
 * Interfaccia a riga di comando per visualizzare lo stato del gioco.
 */
public class CLIView {

    /**
     * Visualizza un messaggio sullo standard output.
     *
     * @param message messaggio da mostrare
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Renderizza lo stato corrente del circuito, sovrapponendo la posizione
     * dei giocatori.
     *
     * @param gameState stato di gioco da visualizzare
     */
    public void displayGameState(GameState gameState) {
        Track track = gameState.getTrack();
        int width = track.getWidth();
        int height = track.getHeight();
        String[][] grid = new String[height][width];

        // Inizializza la griglia in base al tipo di cella.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellType cell = track.getCell(x, y);
                switch (cell) {
                    case WALL:
                        grid[y][x] = "#";
                        break;
                    case ROAD:
                        grid[y][x] = ".";
                        break;
                    case START:
                        grid[y][x] = "S";
                        break;
                    case FINISH:
                        grid[y][x] = "*";
                        break;
                    case CHECKPOINT:
                        // Mostra il numero del checkpoint
                        Position pos = new Position(x,y);
                        
                       
                        break;
                }
            }
        }
        // Sovrapponi i giocatori (utilizzando la prima lettera del loro nome).
        for (Player player : gameState.getPlayers()) {
            Position pos = player.getPosition();
            if (track.isWithinBounds(pos.getX(), pos.getY())) {
                grid[pos.getY()][pos.getX()] = player.getName().substring(0, 1);
            }
        }
        System.out.println("Stato attuale del circuito:");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }
}