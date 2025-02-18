package it.unicam.cs.mdp.vectorrace.view.output;

/**
 * Handler for console output.
 * Provides methods for displaying messages on the console.
 */
public class ConsoleOutputHandler implements IOutputHandler {

    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public void printError(String message) {
        System.err.println("ERROR: " + message);
    }

    /**
     * Clears the console screen (limited support).
     */
    public void clear() {
        // Attempt to clear the console on Unix-like systems
        System.out.print("\033[H\033[2J");
        System.out.flush();

        
        // If the method above fails, print some empty lines
        for(int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}