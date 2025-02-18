package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.players.Player;
import java.util.List;

/**
 * Interface for formatting player information.
 * Separates the logic of formatting player information from the visualization.
 */
public interface IPlayerInfoFormatter {
    /**
     * Formats the information of a player.
     *
     * @param player The player whose information to format.
     * @return A formatted string with the player's information.
     */
    String formatPlayerInfo(Player player);

    /**
     * Formats the information of a list of players.
     *
     * @param players The list of players.
     * @return A formatted string with the information of all players.
     */
    String formatPlayersInfo(List<Player> players);
}