package it.unicam.cs.mdp.vectorrace.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta lo stato corrente della gara.
 */
public class GameState {

    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final Track track;
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean finished;
    private Player winner;

   

    /**
     * Costruttore.
     *
     * @param track   circuito della gara
     * @param players lista di giocatori
     */
    public GameState(Track track, List<Player> players) {
        if (track == null || players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Track and players cannot be null, and players list cannot be empty");
        }
        this.track = track;
        this.players = new ArrayList<>(players); // Crea una copia difensiva
        this.currentPlayerIndex = 0;
        this.finished = false;
        this.winner = null;
    }

    public Track getTrack() {
        return track;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players); // Restituisce una copia difensiva
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Passa al turno del giocatore successivo.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Verifica se il giocatore ha raggiunto la linea di arrivo.
     *
     * @param player giocatore da verificare
     * @return true se il giocatore ha finito la gara
     */
    public boolean checkFinish(Player player) {
        return false;
    }




}
