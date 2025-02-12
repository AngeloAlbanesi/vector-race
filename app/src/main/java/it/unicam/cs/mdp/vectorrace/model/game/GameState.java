package it.unicam.cs.mdp.vectorrace.model.game;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Rappresenta lo stato corrente della gara.
 */
public class GameState {

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
        return this.track;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(this.players); // Restituisce una copia difensiva
    }

    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayerIndex);
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Player getWinner() {
        return this.winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Passa al turno del giocatore successivo.
     */
    public void nextTurn() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();
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
