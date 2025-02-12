package it.unicam.cs.mdp.vectorrace.model.game;

import java.util.Arrays;

import it.unicam.cs.mdp.vectorrace.model.game.validators.WallCollisionValidator;
import it.unicam.cs.mdp.vectorrace.model.game.validators.PlayerCollisionValidator;

/**
 * Factory per creare istanze di MovementManager configurate correttamente.
 * Implementa il Factory Pattern per gestire la creazione complessa degli oggetti.
 */
public class MovementManagerFactory {
    
    /**
     * Crea una nuova istanza di MovementManager con i validatori standard.
     * 
     * @return MovementManager configurato con WallCollisionValidator e PlayerCollisionValidator
     */
    public static MovementManager createDefault() {
        return new MovementManager(Arrays.asList(
            new WallCollisionValidator(),
            new PlayerCollisionValidator()
        ));
    }
}