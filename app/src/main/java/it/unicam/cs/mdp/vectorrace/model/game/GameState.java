package it.unicam.cs.mdp.vectorrace.model.game;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
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
    private final boolean isTemporary;

    /**
     * Costruttore per il gioco normale.
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
        this.isTemporary = false;
    }

    /**
     * Costruttore per stati temporanei usati nel pathfinding.
     * Non richiede una lista di giocatori valida.
     *
     * @param track circuito della gara
     * @param temporary deve essere true per indicare che è uno stato temporaneo
     */
    public GameState(Track track, boolean temporary) {
        if (track == null || !temporary) {
            throw new IllegalArgumentException("Track cannot be null and temporary must be true");
        }
        this.track = track;
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.finished = false;
        this.winner = null;
        this.isTemporary = true;
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
     * Verifica se questo è uno stato temporaneo usato per il pathfinding.
     *
     * @return true se è uno stato temporaneo, false altrimenti
     */
    public boolean isTemporary() {
        return this.isTemporary;
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
        Position pos = player.getPosition();
        return track.getCell(pos.getX(), pos.getY()) == CellType.FINISH;
    }

}
