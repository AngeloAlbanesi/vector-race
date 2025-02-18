package it.unicam.cs.mdp.vectorrace.model.players;

/**
 * Defines the available AI strategy types for bot players in the Vector Race game.
 * Each strategy represents a different pathfinding algorithm that bots can use
 * to navigate the race track.
 * 
 * <p>Available strategies:
 * <ul>
 *   <li>{@link #BFS} (code 1) - Breadth-First Search algorithm
 *     <ul>
 *       <li>Guarantees shortest path in terms of moves</li>
 *       <li>Explores all possible paths systematically</li>
 *       <li>Default strategy if none specified</li>
 *     </ul>
 *   </li>
 *   <li>{@link #ASTAR} (code 2) - A* Search algorithm
 *     <ul>
 *       <li>Uses heuristics to find optimal paths</li>
 *       <li>More efficient than BFS for complex tracks</li>
 *       <li>Considers distance to goal in path planning</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public enum StrategyType {
    /** 
     * Breadth-First Search strategy.
     * Systematically explores all possible moves to find the shortest path.
     */
    BFS(1),
    
    /**
     * A* Search strategy.
     * Uses heuristics to find optimal paths more efficiently.
     */
    ASTAR(2);

    private final int code;

    /**
     * Creates a new strategy type with the specified code.
     *
     * @param code The numeric code representing this strategy.
     */
    StrategyType(int code) {
        this.code = code;
    }

    /**
     * Gets the numeric code associated with this strategy.
     *
     * @return The strategy's numeric code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Converts a numeric code into its corresponding strategy type.
     * If the code doesn't match any strategy, returns {@link #BFS} as default.
     *
     * @param code The numeric code to convert.
     * @return The corresponding strategy type, or BFS if code is invalid.
     */
    public static StrategyType fromCode(int code) {
        for (StrategyType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return BFS; // Default strategy
    }
}