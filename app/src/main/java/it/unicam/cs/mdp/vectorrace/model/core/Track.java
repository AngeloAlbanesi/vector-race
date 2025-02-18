package it.unicam.cs.mdp.vectorrace.model.core;

import java.util.*;

import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.PriorityData;

/**
 * Represents the race track as a grid of cells in the Vector Race game.
 * This class manages the track layout, including walls, checkpoints, start and finish lines.
 * 
 * <p>The track is implemented as a 2D grid where:
 * <ul>
 *   <li>The origin (0,0) is at the top-left corner</li>
 *   <li>X coordinates increase from left to right</li>
 *   <li>Y coordinates increase from top to bottom</li>
 * </ul>
 * 
 * <p>Each cell in the grid has a specific {@link CellType} that determines its role
 * in the game (wall, road, checkpoint, etc.).
 */
public class Track {
    private final CellType[][] grid;
    private final int width;
    private final int height;
    private final Map<Position, PriorityData> checkpointData;

    /**
     * Creates a new track with the specified grid layout and checkpoint data.
     * 
     * @param grid The 2D array representing the track layout, where each cell
     *             contains a {@link CellType} value.
     * @param checkpointData Map associating checkpoint positions with their metadata,
     *                       including order and completion status.
     */
    public Track(CellType[][] grid, Map<Position, PriorityData> checkpointData) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
        this.checkpointData = checkpointData;
    }

    /**
     * Gets the width of the track (number of columns).
     *
     * @return The track width in cells.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the height of the track (number of rows).
     *
     * @return The track height in cells.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Checks if the given coordinates are within the track boundaries.
     *
     * @param x The x coordinate to check.
     * @param y The y coordinate to check.
     * @return true if the coordinates are within bounds, false otherwise.
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < this.width && y >= 0 && y < this.height;
    }

    /**
     * Gets the cell type at the specified coordinates.
     * Returns {@link CellType#WALL} for out-of-bounds coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The type of the cell at the specified position.
     */
    public CellType getCell(int x, int y) {
        if (!isWithinBounds(x, y))
            return CellType.WALL;
        return this.grid[y][x];
    }

    /**
     * Checks if a cell at the given coordinates can be traversed by players.
     * A cell is passable if it's a road, start line, finish line, or checkpoint.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return true if the cell is passable, false otherwise.
     */
    public boolean isPassable(int x, int y) {
        CellType cell = getCell(x, y);
        return cell == CellType.ROAD || cell == CellType.START ||
                cell == CellType.FINISH || cell == CellType.CHECKPOINT;
    }

    /**
     * Checks if the cell at the given coordinates is a start position.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return true if the cell is a start position, false otherwise.
     */
    public boolean isStart(int x, int y) {
        return getCell(x, y) == CellType.START;
    }

    /**
     * Checks if the cell at the given coordinates is a finish line.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return true if the cell is part of the finish line, false otherwise.
     */
    public boolean isFinish(int x, int y) {
        return getCell(x, y) == CellType.FINISH;
    }

    /**
     * Gets all finish line positions on the track.
     * This method scans the entire grid to find all cells marked as {@link CellType#FINISH}.
     *
     * @return A list of positions representing the finish line cells.
     */
    public List<Position> getFinishPositions() {
        List<Position> finishPositions = new ArrayList<>();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (isFinish(x, y)) {
                    finishPositions.add(new Position(x, y));
                }
            }
        }
        return finishPositions;
    }

    /**
     * Gets the checkpoint number at the specified position.
     * Checkpoints are numbered sequentially to determine their order.
     *
     * @param pos The position to check.
     * @return The checkpoint number, or -1 if the position is not a checkpoint.
     */
    public int getCheckpointNumber(Position pos) {
        PriorityData data = this.checkpointData.get(pos);
        return data != null ? data.getCheckpointNumber() : -1;
    }

    /**
     * Gets the highest checkpoint number present on the track.
     * This represents the total number of checkpoints that must be reached.
     *
     * @return The maximum checkpoint number.
     */
    public int getMaxCheckpoint() {
        int max = 0;
        for (PriorityData data : this.checkpointData.values()) {
            max = Math.max(max, data.getCheckpointNumber());
        }
        return max;
    }

    /**
     * Checks if a checkpoint of the specified number has been reached in the given row.
     * This is used to validate checkpoint completion in the correct order.
     *
     * @param y The y coordinate of the row to check.
     * @param checkpointNum The checkpoint number to look for.
     * @return true if a checkpoint with the given number has been reached in the specified row.
     */
    public boolean hasReachedCheckpointInRow(int y, int checkpointNum) {
        for (Map.Entry<Position, PriorityData> entry : this.checkpointData.entrySet()) {
            Position pos = entry.getKey();
            PriorityData data = entry.getValue();
            if (pos.getY() == y && data.getCheckpointNumber() == checkpointNum && data.isReached()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Marks a checkpoint as reached at the specified position.
     * This updates the checkpoint's completion status in the track data.
     *
     * @param pos The position of the checkpoint to mark as reached.
     */
    public void setCheckpointReached(Position pos) {
        PriorityData data = this.checkpointData.get(pos);
        if (data != null) {
            data.setReached(true);
        }
    }
}
