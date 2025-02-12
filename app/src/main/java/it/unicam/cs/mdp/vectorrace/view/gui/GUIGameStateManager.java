package it.unicam.cs.mdp.vectorrace.view.gui;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.HumanPlayer;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Gestisce lo stato del gioco e la sua evoluzione.
 * Implementa la logica di avanzamento turni, collisioni e vittoria.
 */
public class GUIGameStateManager {
    private final GameState gameState;
    private final MovementManager movementManager;
    private final Consumer<String> statusUpdateCallback;
    private Set<Position> validMoves;

    public GUIGameStateManager(GameState gameState, MovementManager movementManager, Consumer<String> statusUpdateCallback) {
        this.gameState = gameState;
        this.movementManager = movementManager;
        this.statusUpdateCallback = statusUpdateCallback;
        this.validMoves = new HashSet<>();
        updateValidMoves();
    }

    /**
     * Avanza il gioco di un turno.
     */
    public void advanceTurn() {
        Player currentPlayer = gameState.getCurrentPlayer();
        processPlayerTurn(currentPlayer);
        
        if (!gameState.isFinished()) {
            gameState.nextTurn();
            updateValidMoves();
        }
    }

    private void processPlayerTurn(Player currentPlayer) {
        Vector acceleration = getPlayerAcceleration(currentPlayer);
        if (acceleration == null) {
            statusUpdateCallback.accept(currentPlayer.getName() + " non ha fornito un'accelerazione valida.");
            acceleration = new Vector(0, 0);
        }

        if (!validatePlayerMove(currentPlayer, acceleration)) {
            return;
        }

        updatePlayerPosition(currentPlayer, acceleration);
        checkVictoryCondition(currentPlayer);
    }

    private Vector getPlayerAcceleration(Player player) {
        return player.getNextAcceleration(gameState);
    }

    private boolean validatePlayerMove(Player player, Vector acceleration) {
        if (!movementManager.validateMove(player, acceleration, gameState)) {
            statusUpdateCallback.accept(
                player.getName() + " ha colliso con un muro o giocatore fermo! Velocità resettata."
            );
            player.resetVelocity();
            return false;
        }
        return true;
    }

    private void updatePlayerPosition(Player player, Vector acceleration) {
        Vector newVelocity = player.getVelocity().add(acceleration);
        Position newPosition = player.getPosition().move(newVelocity);

        if (isPositionOccupiedByOtherPlayer(newPosition, player)) {
            statusUpdateCallback.accept(
                player.getName() + " ha trovato la cella occupata da un altro giocatore, resta fermo!"
            );
            player.resetVelocity();
            return;
        }

        player.updatePosition(newPosition);
        player.updateVelocity(newVelocity);
    }

    private boolean isPositionOccupiedByOtherPlayer(Position position, Player currentPlayer) {
        return gameState.getPlayers().stream()
            .filter(p -> p != currentPlayer)
            .anyMatch(p -> p.getPosition().equals(position));
    }

    private void checkVictoryCondition(Player player) {
        Position pos = player.getPosition();
        if (gameState.getTrack().getCell(pos.getX(), pos.getY()) == CellType.FINISH) {
            handleVictory(player);
        }
    }

    private void handleVictory(Player winner) {
        gameState.setFinished(true);
        gameState.setWinner(winner);
        String victoryMessage = "Il Giocatore " + winner.getName() + " ha vinto la gara!";
        statusUpdateCallback.accept(victoryMessage);

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fine della gara");
            alert.setHeaderText("Vittoria!");
            alert.setContentText(victoryMessage);
            alert.showAndWait().ifPresent(response -> Platform.exit());
        });
    }

    /**
     * Aggiorna l'insieme delle mosse valide per il giocatore corrente.
     */
    private void updateValidMoves() {
        if (gameState.getCurrentPlayer() instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) gameState.getCurrentPlayer();
            validMoves = humanPlayer.calculateValidMoves(gameState);
        } else {
            validMoves.clear();
        }
    }

    public Set<Position> getValidMoves() {
        return validMoves;
    }

    public GameState getGameState() {
        return gameState;
    }
}