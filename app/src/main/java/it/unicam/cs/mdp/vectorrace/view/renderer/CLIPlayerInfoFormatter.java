package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.players.Player;
import java.util.List;

/**
 * Implementazione CLI del formattatore di informazioni dei giocatori.
 * Si occupa esclusivamente della formattazione delle informazioni dei giocatori
 * per la visualizzazione CLI.
 */
public class CLIPlayerInfoFormatter implements IPlayerInfoFormatter {

    @Override
    public String formatPlayerInfo(Player player) {
        return String.format("%s: Posizione=%s, Velocità=%s",
                player.getName(),
                player.getPosition(),
                player.getVelocity());
    }

    @Override
    public String formatPlayersInfo(List<Player> players) {
        StringBuilder info = new StringBuilder("\n=== Stato dei giocatori ===\n");
        for (Player player : players) {
            info.append(formatPlayerInfo(player)).append("\n");
        }
        return info.toString();
    }
}