package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.players.Player;
import java.util.List;

/**
 * Interfaccia per la formattazione delle informazioni dei giocatori.
 * Separa la logica di formattazione delle informazioni dei giocatori dalla visualizzazione.
 */
public interface IPlayerInfoFormatter {
    /**
     * Formatta le informazioni di un giocatore.
     * @param player il giocatore di cui formattare le informazioni
     * @return stringa formattata con le informazioni del giocatore
     */
    String formatPlayerInfo(Player player);

    /**
     * Formatta le informazioni di una lista di giocatori.
     * @param players la lista dei giocatori
     * @return stringa formattata con le informazioni di tutti i giocatori
     */
    String formatPlayersInfo(List<Player> players);
}