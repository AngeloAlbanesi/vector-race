package it.unicam.cs.mdp.vectorrace.model.ai;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Vector;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.game.MovementManager;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Implementazione della strategia A* per il movimento del bot.
 * Utilizza un algoritmo A* puro senza penalità e fallback.
 */
public class PureAStarStrategy implements AIStrategy {
    private final IPathFinder pathFinder;
    private final CheckpointTargetFinder targetFinder;

    /**
     * Costruisce una nuova strategia A*.
     * Inizializza tutte le dipendenze necessarie con le implementazioni di default.
     */
    public PureAStarStrategy() {
        this.targetFinder = new CheckpointTargetFinder();
        this.pathFinder = new AStarPathFinder(
            new ChebyshevHeuristic(),
            new MovementManager(),
            new CheckpointManager()
        );
    }

    /**
     * Costruttore per dependency injection, utile per i test.
     */
    public PureAStarStrategy(IPathFinder pathFinder, CheckpointTargetFinder targetFinder) {
        this.pathFinder = pathFinder;
        this.targetFinder = targetFinder;
    }

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        Position target = targetFinder.findNextTarget(player, gameState);
        if (target == null) {
            return new Vector(0, 0);
        }
        
        return pathFinder.findPath(player, gameState, target);
    }
}
