package it.unicam.cs.mdp.vectorrace.model.ai.services;

import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;

/**
 * Defines the operations for locating the finish line cell on the track.
 * Implementations of this interface are responsible for finding the
 * position of the finish line, which is the target for players to complete the race.
 *
 * <p>The locator provides a method to efficiently search the track and
 * identify the finish line cell.
 */
public interface IFinishLocator {
    /**
     * Locates the finish line cell on the track.
     *
     * @param track The track on which to search for the finish line.
     * @return The position of the finish line cell, or null if not found.
     */
    Position locateFinish(Track track);
}