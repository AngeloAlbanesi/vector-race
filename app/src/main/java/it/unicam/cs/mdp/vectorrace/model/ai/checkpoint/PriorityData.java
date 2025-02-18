package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

/**
 * Represents the priority data for a checkpoint in the Vector Race game.
 * This class encapsulates information about the checkpoint's level of priority,
 * its sequence number within the track, and its completion status.
 *
 * <p>Key attributes:
 * <ul>
 *   <li>priorityLevel - The level of priority (1 = low, 2 = medium, 3 = high)</li>
 *   <li>checkpointNumber - The sequential number of the checkpoint on the track</li>
 *   <li>completed - A flag indicating if the checkpoint has been fully completed</li>
 *   <li>reached - A flag indicating if the checkpoint has been reached during the current move</li>
 * </ul>
 */
public class PriorityData {
    private final int priorityLevel; // Priority level (1 = low, 2 = medium, 3 = high)
    private final int checkpointNumber; // Sequential number of the checkpoint on the track
    private boolean completed; // Flag indicating if the checkpoint has been fully completed
    private boolean reached; // Flag indicating if the checkpoint has been reached

    /**
     * Creates a new PriorityData instance.
     *
     * @param priorityLevel The priority level of the checkpoint.
     * @param checkpointNumber The sequential number of the checkpoint.
     */
    public PriorityData(int priorityLevel, int checkpointNumber) {
        this.priorityLevel = priorityLevel;
        this.checkpointNumber = checkpointNumber;
        this.completed = false;
        this.reached = false;
    }

    /**
     * Gets the priority level of the checkpoint.
     *
     * @return The priority level (1 = low, 2 = medium, 3 = high).
     */
    public int getPriorityLevel() {
        return priorityLevel;
    }

    /**
     * Gets the sequential number of the checkpoint on the track.
     *
     * @return The checkpoint number.
     */
    public int getCheckpointNumber() {
        return checkpointNumber;
    }

    /**
     * Checks if the checkpoint has been fully completed.
     *
     * @return true if the checkpoint is completed, false otherwise.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the completed status of the checkpoint.
     *
     * @param completed The new completed status.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Checks if the checkpoint has been reached during the current move.
     *
     * @return true if the checkpoint has been reached, false otherwise.
     */
    public boolean isReached() {
        return reached;
    }

    /**
     * Sets the reached status of the checkpoint.
     *
     * @param reached The new reached status.
     */
    public void setReached(boolean reached) {
        this.reached = reached;
    }

    @Override
    public String toString() {
        return String.format("Priority: %d, Number: %d, Completed: %b, Reached: %b",
                priorityLevel, checkpointNumber, completed, reached);
    }
}