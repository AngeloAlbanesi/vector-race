package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;

/**
 * Default implementation of the {@link IFinishLocator} interface.
 * This class is responsible for locating the finish line cell on the track
 * for the Vector Race game.
 *
 * <p>The locator scans the entire track grid to find a cell marked as
 * the finish line ({@link CellType#FINISH}).
 */
public class FinishCellLocator implements IFinishLocator {
    
    @Override
    public Position locateFinish(Track track) {
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.getCell(x, y) == CellType.FINISH) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }
}