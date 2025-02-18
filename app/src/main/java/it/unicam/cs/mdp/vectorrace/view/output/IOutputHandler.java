package it.unicam.cs.mdp.vectorrace.view.output;

/**
 * Interface for managing the game output.
 * Defines the basic methods for displaying messages.
 */
public interface IOutputHandler {

    /**
     * Displays a string without a newline.
     *
     * @param message The message to display.
     */
    void print(String message);

    /**
     * Displays a string with a newline.
     *
     * @param message The message to display.
     */
    void println(String message);

    /**
     * Displays an error message.
     *
     * @param message The error message to display.
     */
    void printError(String message);

    /**
     * Clears the screen.
     */
    void clear();

    /**
     * Forces the flush of the output buffer.
     */
    default void flush() {
        System.out.flush();
    }
}