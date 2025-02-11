package it.unicam.cs.mdp.vectorrace.model.game;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.view.GameView;

/**
 * Classe responsabile della gestione dei turni di gioco.
 * Implementa la logica di movimento, collisioni e vittoria.
 */
public class TurnManager {
    private final GameState gameState;
    private final MovementManager movementManager;
    private final GameView view;

    public TurnManager(GameState gameState, MovementManager movementManager, GameView view) {
        this.gameState = gameState;
        this.movementManager = movementManager;
        this.view = view;
    }

    /**
     * Gestisce l'avanzamento di un turno di gioco.
     */
    public void advanceTurn() {
        Player currentPlayer = gameState.getCurrentPlayer();
        
        // Ottieni e valida l'accelerazione
        Vector acceleration = getAndValidateAcceleration(currentPlayer);
        
        // Processa il movimento
        if (processMovement(currentPlayer, acceleration)) {
            // Se il movimento è valido, verifica la vittoria
            if (checkVictoryConditions(currentPlayer)) {
                endGame(currentPlayer);
                return;
            }
        }

        // Passa al prossimo turno
        gameState.nextTurn();
        view.displayGameState(gameState);
    }

    /**
     * Ottiene e valida l'accelerazione dal giocatore.
     */
    private Vector getAndValidateAcceleration(Player player) {
        Vector acceleration = player.getNextAcceleration(gameState);
        if (acceleration == null) {
            view.displayMessage(player.getName() + " non ha fornito un'accelerazione valida.");
            return new Vector(0, 0);
        }
        return acceleration;
    }

    /**
     * Processa il movimento del giocatore.
     * @return true se il movimento è stato completato con successo
     */
    private boolean processMovement(Player player, Vector acceleration) {
        // Valida il movimento
        if (!movementManager.validateMove(player, acceleration, gameState)) {
            handleCollision(player);
            return false;
        }

        // Calcola la nuova posizione
        Vector newVelocity = player.getVelocity().add(acceleration);
        Position newPosition = player.getPosition().move(newVelocity);

        // Verifica sovrapposizione con altri giocatori
        if (isPositionOccupied(newPosition, player)) {
            handleOccupiedPosition(player);
            return false;
        }

        // Applica il movimento
        player.updatePosition(newPosition);
        player.updateVelocity(newVelocity);
        return true;
    }

    /**
     * Verifica se una posizione è occupata da altri giocatori.
     */
    private boolean isPositionOccupied(Position position, Player currentPlayer) {
        return gameState.getPlayers().stream()
                .anyMatch(other -> other != currentPlayer && other.getPosition().equals(position));
    }

    /**
     * Gestisce una collisione con un muro o ostacolo.
     */
    private void handleCollision(Player player) {
        view.displayMessage(player.getName() + " ha colliso con un muro o giocatore fermo! Velocità resettata.");
        player.resetVelocity();
    }

    /**
     * Gestisce il caso di una posizione occupata da un altro giocatore.
     */
    private void handleOccupiedPosition(Player player) {
        view.displayMessage(player.getName() + " ha trovato la cella occupata da un altro giocatore, resta fermo!");
        player.resetVelocity();
    }

    /**
     * Verifica le condizioni di vittoria per un giocatore.
     */
    private boolean checkVictoryConditions(Player player) {
        Position pos = player.getPosition();
        return gameState.getTrack().getCell(pos.getX(), pos.getY()) == CellType.FINISH || 
               gameState.checkFinish(player);
    }

    /**
     * Gestisce la fine del gioco quando un giocatore vince.
     */
    private void endGame(Player winner) {
        gameState.setFinished(true);
        gameState.setWinner(winner);
        view.displayMessage("Il Giocatore " + winner.getName() + " ha vinto la gara!");
        view.displayGameState(gameState);
    }
}