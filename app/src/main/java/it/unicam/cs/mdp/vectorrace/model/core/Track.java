package it.unicam.cs.mdp.vectorrace.model.core;

import java.util.*;

import it.unicam.cs.mdp.vectorrace.model.ai.checkpoint.PriorityData;

/**
 * Rappresenta il circuito come una griglia di celle.
 */
public class Track {
    private final CellType[][] grid;
    private final int width;
    private final int height;
    private final Map<Position, PriorityData> checkpointData;

    /**
     * Costruttore.
     * 
     * @param grid             matrice dei tipi di celle
     * @param checkpointOrders mappa delle posizioni checkpoint e il loro ordine
     */
    public Track(CellType[][] grid, Map<Position, PriorityData> checkpointData) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
        this.checkpointData = checkpointData;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < this.width && y >= 0 && y < this.height;
    }

    public CellType getCell(int x, int y) {
        if (!isWithinBounds(x, y))
            return CellType.WALL;
        return this.grid[y][x];
    }

    public boolean isPassable(int x, int y) {
        CellType cell = getCell(x, y);
        return cell == CellType.ROAD || cell == CellType.START ||
                cell == CellType.FINISH || cell == CellType.CHECKPOINT;
    }

    public boolean isStart(int x, int y) {
        return getCell(x, y) == CellType.START;
    }

    public boolean isFinish(int x, int y) {
        return getCell(x, y) == CellType.FINISH;
    }

    /**
     * Restituisce la lista di tutte le posizioni di arrivo (celle FINISH).
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
     * Restituisce il numero del checkpoint nella posizione specificata.
     * 
     * @param pos posizione da controllare
     * @return numero del checkpoint, o -1 se non è un checkpoint
     */
    public int getCheckpointNumber(Position pos) {
        PriorityData data = this.checkpointData.get(pos);
        return data != null ? data.getCheckpointNumber() : -1;
    }

    /**
     * Restituisce il numero massimo dei checkpoint presenti nel circuito.
     * 
     * @return numero massimo dei checkpoint
     */
    public int getMaxCheckpoint() {
        int max = 0;
        for (PriorityData data : this.checkpointData.values()) {
            max = Math.max(max, data.getCheckpointNumber());
        }
        return max;
    }

    /**
     * Verifica se è stato già raggiunto un checkpoint con lo stesso numero sulla
     * stessa riga
     * 
     * @param y             coordinata y da controllare
     * @param checkpointNum numero del checkpoint
     * @return true se è già stato raggiunto un checkpoint con lo stesso numero
     *         sulla riga
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
     * Marca un checkpoint come raggiunto
     * 
     * @param pos posizione del checkpoint
     */
    public void setCheckpointReached(Position pos) {
        PriorityData data = this.checkpointData.get(pos);
        if (data != null) {
            data.setReached(true);
        }
    }

}
