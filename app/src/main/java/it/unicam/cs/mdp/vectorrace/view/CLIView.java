package it.unicam.cs.mdp.vectorrace.view;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.model.core.CellType;

/**
 * Interfaccia a riga di comando per visualizzare lo stato del gioco.
 */
public class CLIView {
    private boolean isGameRunning = false;

    /**
     * Visualizza un messaggio sullo standard output.
     *
     * @param message messaggio da mostrare
     */
    public void displayMessage(String message) {
        System.out.println(message);
        System.out.flush(); // Forza l'output immediato
    }

    /**
     * Mostra il menu di selezione del circuito.
     */
    public void showCircuitSelection() {
        System.out.println("\n=== Vector Race - Selezione Circuito ===");
        System.out.println("1. Circuito 1");
        System.out.println("2. Circuito 2");
        System.out.println("3. Circuito 3");
        System.out.print("Seleziona un circuito (1-3): ");
        System.out.flush(); // Forza l'output immediato
    }

    /**
     * Mostra il menu principale del gioco.
     */
    public void showGameMenu() {
        System.out.println("\n=== Menu di Gioco ===");
        System.out.println("1. Avvia simulazione");
        System.out.println("2. Avanza di un turno");
        System.out.print("Seleziona un'opzione (1-2): ");
        System.out.flush(); // Forza l'output immediato
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

        // Inizializza la griglia in base al tipo di cella
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellType cell = track.getCell(x, y);
                switch (cell) {
                    case WALL:
                        grid[y][x] = "#";
                        break;
                    case ROAD:
                    case CHECKPOINT:
                        grid[y][x] = ".";
                        break;
                    case START:
                        grid[y][x] = "S";
                        break;
                    case FINISH:
                        grid[y][x] = "*";
                        break;
                }
            }
        }

        // Sovrapponi i giocatori (utilizzando il numero dal nome del bot)
        for (Player player : gameState.getPlayers()) {
            Position pos = player.getPosition();
            if (track.isWithinBounds(pos.getX(), pos.getY())) {
                // Estrai il numero dal nome del bot (es: "Bot1" -> "1")
                String playerNumber = player.getName().replaceAll("\\D+", "");
                grid[pos.getY()][pos.getX()] = playerNumber;
            }
        }

        // Stampa lo stato del gioco
        clearScreen();
        System.out.println("=== Stato attuale del circuito ===");
        for (int y = 0; y < height; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                line.append(grid[y][x]);
            }
            System.out.println(line.toString());
            System.out.flush(); // Forza l'output di ogni riga
        }

        // Stampa informazioni dei giocatori
        System.out.println("\n=== Stato dei giocatori ===");
        for (Player player : gameState.getPlayers()) {
            System.out.printf("%s: Posizione=%s, Velocità=%s, Checkpoint=%d%n",
                    player.getName(),
                    player.getPosition(),
                    player.getVelocity(),
                    player.getNextCheckpointIndex());
            System.out.flush(); // Forza l'output di ogni riga di stato
        }

        if (gameState.isFinished()) {
            Player winner = gameState.getWinner();
            if (winner != null) {
                System.out.println("\n🏆 Il Giocatore " + winner.getName() + " ha vinto la gara! 🏆");
                System.out.flush();
                // Aspetta un secondo per assicurarsi che l'utente veda il messaggio
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // Forza la terminazione del programma
                System.exit(0);
            }
        }

        // Mostra automaticamente il menu dopo lo stato del gioco
        showGameMenu();

        // Lascia una riga vuota per separare lo stato dal menu
        System.out.println();
        System.out.flush();
    }

    /**
     * Pulisce lo schermo del terminale
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Imposta lo stato di esecuzione del gioco
     */
    public void setGameRunning(boolean running) {
        this.isGameRunning = running;
    }

    /**
     * Restituisce lo stato di esecuzione del gioco
     */
    public boolean isGameRunning() {
        return this.isGameRunning;
    }
}