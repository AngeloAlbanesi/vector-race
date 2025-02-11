package it.unicam.cs.mdp.vectorrace.model.ai.checkpoint;

/**
 * Classe che rappresenta i dati di priorità di un checkpoint.
 */
public class PriorityData {
    private final int priorityLevel; // Livello di priorità (1 = bassa, 2 = media, 3 = alta)
    private final int checkpointNumber; // Numero progressivo del checkpoint nel circuito
    private boolean completed; // Flag che indica se il checkpoint è stato superato
    private boolean reached; // Flag che indica se il checkpoint è stato raggiunto

    public PriorityData(int priorityLevel, int checkpointNumber) {
        this.priorityLevel = priorityLevel;
        this.checkpointNumber = checkpointNumber;
        this.completed = false;
        this.reached = false;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public int getCheckpointNumber() {
        return checkpointNumber;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isReached() {
        return reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    @Override
    public String toString() {
        return String.format("Priority: %d, Number: %d, Completed: %b, Reached: %b",
                priorityLevel, checkpointNumber, completed, reached);
    }
}