package it.unicam.cs.mdp.vectorrace.model.game;

import java.util.List;
import java.util.ArrayList;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.validators.MoveValidator;
import it.unicam.cs.mdp.vectorrace.model.game.validators.WallCollisionValidator;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Gestisce la validazione dei movimenti nel gioco delegando ai validatori specifici.
 */
public class MovementManager {
    
    private final List<MoveValidator> validators;
    private final WallCollisionValidator wallValidator;
    
    /**
     * Costruttore di default che utilizza la factory per inizializzare i validatori standard.
     */
    public MovementManager() {
        MovementManager defaultManager = MovementManagerFactory.createDefault();
        this.validators = defaultManager.validators;
        // Il primo validatore è sempre WallCollisionValidator per convenzione
        this.wallValidator = (WallCollisionValidator) validators.get(0);
    }
    
    /**
     * Costruttore che accetta una lista di validatori.
     * Implementa la Dependency Injection per maggiore flessibilità.
     *
     * @param validators lista dei validatori da utilizzare
     */
    public MovementManager(List<MoveValidator> validators) {
        if (!(validators.get(0) instanceof WallCollisionValidator)) {
            throw new IllegalArgumentException("Il primo validatore deve essere WallCollisionValidator");
        }
        this.validators = new ArrayList<>(validators);
        this.wallValidator = (WallCollisionValidator) validators.get(0);
    }
    
    /**
     * Verifica se un giocatore può effettuare la mossa (accelerazione),
     * controllando tutti i validatori configurati.
     */
    public boolean validateMove(Player player, Vector acceleration, GameState gameState) {
        Vector newVelocity = player.getVelocity().add(acceleration);
        Position newPosition = player.getPosition().move(newVelocity);
        
        return isMovementValid(player.getPosition(), newPosition, player, gameState);
    }
    
    /**
     * Verifica se il movimento è valido controllando tutti i validatori.
     * Il movimento è valido solo se tutti i validatori lo approvano.
     */
    private boolean isMovementValid(Position start, Position end, Player player, GameState gameState) {
        return validators.stream()
                .allMatch(validator -> validator.isValidMove(start, end, player, gameState));
    }
    
    /**
     * Versione semplificata per il pathfinding che usa solo il validatore dei muri.
     * Questo metodo mantiene la compatibilità con il codice esistente.
     */
    public boolean validateMoveTemp(Position start, Vector velocity, Track track) {
        Position end = start.add(velocity);
        return wallValidator.isValidMove(start, end, null, new GameState(track, true));
    }
}
