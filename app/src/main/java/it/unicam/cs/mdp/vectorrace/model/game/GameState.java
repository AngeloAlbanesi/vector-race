package it.unicam.cs.mdp.vectorrace.model.game;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Represents the current state of a Vector Race game.
 * This class maintains all the information necessary to describe the game's current condition,
 * including the track, players, turn order, and game completion status.
 * 
 * <p>The state can be either:
 * <ul>
 *   <li>A regular game state used during actual gameplay</li>
 *   <li>A temporary state used for pathfinding calculations</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Immutable track reference</li>
 *   <li>Defensive copies for collections</li>
 *   <li>Turn management</li>
 *   <li>Game completion tracking</li>
 * </ul>
 */
public class GameState {

    private final Track track;
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean finished;
    private Player winner;
    private final boolean isTemporary;

    /**
     * Creates a new regular game state.
     * This constructor is used for actual gameplay scenarios where a complete
     * track and list of players are required.
     *
     * @param track The race track for the game.
     * @param players The list of players participating in the game.
     * @throws IllegalArgumentException if track is null, players is null or empty.
     */
    public GameState(Track track, List<Player> players) {
        if (track == null || players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Track and players cannot be null, and players list cannot be empty");
        }
        this.track = track;
        this.players = new ArrayList<>(players); // Creates a defensive copy
        this.currentPlayerIndex = 0;
        this.finished = false;
        this.winner = null;
        this.isTemporary = false;
    }

    /**
     * Creates a temporary game state for pathfinding calculations.
     * This constructor is specifically designed for AI pathfinding operations
     * where a complete player list is not necessary.
     *
     * @param track The race track for pathfinding calculations.
     * @param temporary Must be true to indicate this is a temporary state.
     * @throws IllegalArgumentException if track is null or temporary is false.
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

    /**
     * Gets the race track associated with this game state.
     *
     * @return The current track.
     */
    public Track getTrack() {
        return this.track;
    }

    /**
     * Gets a defensive copy of the player list.
     * Returns a new list to prevent external modifications to the internal state.
     *
     * @return A new list containing all players.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    /**
     * Gets the index of the current player in the turn order.
     *
     * @return The current player's index.
     */
    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    /**
     * Gets the player whose turn it currently is.
     *
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayerIndex);
    }

    /**
     * Checks if the game has finished.
     *
     * @return true if the game is finished, false otherwise.
     */
    public boolean isFinished() {
        return this.finished;
    }

    /**
     * Sets the game's finished status.
     *
     * @param finished The new finished status.
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Gets the winner of the game, if any.
     *
     * @return The winning player, or null if the game hasn't been won yet.
     */
    public Player getWinner() {
        return this.winner;
    }

    /**
     * Sets the winner of the game.
     *
     * @param winner The player who has won the game.
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Checks if this is a temporary state used for pathfinding.
     *
     * @return true if this is a temporary state, false if it's a regular game state.
     */
    public boolean isTemporary() {
        return this.isTemporary;
    }

    /**
     * Advances the game to the next player's turn.
     * Implements a circular rotation through the player list.
     */
    public void nextTurn() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();
    }

    /**
     * Checks if a player has reached the finish line.
     * A player is considered to have finished when their position
     * coincides with a finish line cell.
     *
     * @param player The player to check.
     * @return true if the player has reached the finish line, false otherwise.
     */
    public boolean checkFinish(Player player) {
        Position pos = player.getPosition();
        return track.getCell(pos.getX(), pos.getY()) == CellType.FINISH;
    }
}
