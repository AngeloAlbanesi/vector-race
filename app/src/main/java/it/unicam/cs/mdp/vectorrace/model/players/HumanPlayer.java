// HumanPlayer.java
package it.unicam.cs.mdp.vectorrace.model.players;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;

/**
 * Rappresenta un giocatore controllato da un utente umano. L'input viene
 * fornito tramite interfaccia GUI JavaFX.
 */
public class HumanPlayer extends Player {
    private final MovementManager movementManager;
    private Vector selectedAcceleration;
    private Set<Position> validMoves;
    private static final Color HIGHLIGHT_COLOR = Color.decode("#90EE90");

    /**
     * Costruttore.
     *
     * @param name          nome del giocatore
     * @param color         colore per visualizzazione
     * @param startPosition posizione iniziale
     */
    public HumanPlayer(String name, Color color, Position startPosition) {
        super(name, color, startPosition);
        this.movementManager = new MovementManager();
        this.validMoves = new HashSet<>();
        this.selectedAcceleration = null;
    }

    /**
     * Calcola tutte le mosse valide disponibili per il giocatore.
     * 
     * @param gameState stato corrente del gioco
     * @return Set di posizioni valide
     */
    public Set<Position> calculateValidMoves(GameState gameState) {
        validMoves.clear();
        // Controllo delle 8 direzioni possibili (-1,0,1 per x e y)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Vector acceleration = new Vector(dx, dy);
                Vector newVelocity = getVelocity().add(acceleration);
                Position newPosition = getPosition().move(newVelocity);

                if (movementManager.validateMove(this, acceleration, gameState)) {
                    validMoves.add(newPosition);
                }
            }
        }
        return validMoves;
    }

    /**
     * Verifica se una posizione è una mossa valida.
     */
    public boolean isValidMove(Position position) {
        return validMoves.contains(position);
    }

    /**
     * Imposta l'accelerazione selezionata dall'utente.
     */
    public void setSelectedAcceleration(Vector acceleration) {
        this.selectedAcceleration = acceleration;
    }

    /**
     * Restituisce il colore di evidenziazione per le mosse valide.
     */
    public Color getHighlightColor() {
        return HIGHLIGHT_COLOR;
    }

    @Override
    public Vector getNextAcceleration(GameState gameState) {
        // Restituisce l'accelerazione selezionata dall'utente o (0,0) se nessuna
        // selezione
        return selectedAcceleration != null ? selectedAcceleration : new Vector(0, 0);
    }

    /**
     * Resetta la selezione dell'accelerazione.
     */
    public void resetSelection() {
        this.selectedAcceleration = null;
        this.validMoves.clear();
    }
}