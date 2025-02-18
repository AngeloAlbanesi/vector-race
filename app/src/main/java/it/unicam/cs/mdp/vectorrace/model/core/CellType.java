package it.unicam.cs.mdp.vectorrace.model.core;

/**
 * Represents the different types of cells that can exist on the race track.
 * Each cell type has specific properties and roles in the game:
 * 
 * <ul>
 *   <li>{@link #WALL} - Impassable cell that represents track boundaries</li>
 *   <li>{@link #ROAD} - Basic traversable cell where players can move</li>
 *   <li>{@link #START} - Starting line cells where players begin the race</li>
 *   <li>{@link #FINISH} - Finish line cells that mark the race end</li>
 *   <li>{@link #CHECKPOINT} - Intermediate points that players must pass through</li>
 * </ul>
 */
public enum CellType {
    /** Represents a wall or barrier that cannot be traversed */
    WALL,
    
    /** Represents a normal road cell that can be traversed */
    ROAD,
    
    /** Represents a starting position where players can begin the race */
    START,
    
    /** Represents the finish line that players must reach to complete the race */
    FINISH,
    
    /** Represents a checkpoint that players must pass through during the race */
    CHECKPOINT;

    /**
     * Converts a character from the track file into its corresponding CellType.
     * The mapping follows these rules:
     * <ul>
     *   <li>'#' → {@link #WALL}</li>
     *   <li>'.' → {@link #ROAD}</li>
     *   <li>'S' → {@link #START}</li>
     *   <li>'*' → {@link #FINISH}</li>
     *   <li>Any digit → {@link #CHECKPOINT}</li>
     *   <li>Any other character → {@link #ROAD} (default)</li>
     * </ul>
     *
     * @param c The character from the track file to convert.
     * @return The corresponding CellType. Returns ROAD for unrecognized characters.
     */
    public static CellType fromChar(char c) {
        if (Character.isDigit(c)) {
            return CHECKPOINT;
        }
        switch (c) {
            case '#':
                return WALL;
            case '.':
                return ROAD;
            case 'S':
                return START;
            case '*':
                return FINISH;
            default:
                return ROAD; // Default to ROAD for unrecognized characters
        }
    }
}