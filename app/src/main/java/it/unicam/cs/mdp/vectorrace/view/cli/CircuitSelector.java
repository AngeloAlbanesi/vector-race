package it.unicam.cs.mdp.vectorrace.view.cli;

import it.unicam.cs.mdp.vectorrace.config.CLIConfig;
import it.unicam.cs.mdp.vectorrace.view.CLIView;

/**
 * The {@code CircuitSelector} class manages the selection of the game circuit
 * through the command-line interface (CLI).
 * It allows the user to choose from the available circuits, validating the input
 * and providing appropriate feedback in case of errors.
 */
public class CircuitSelector {
    private final CLIView view;

    /**
     * Constructor for the {@code CircuitSelector} class.
     *
     * @param view The instance of {@code CLIView} used to interact with the user.
     */
    public CircuitSelector(CLIView view) {
        this.view = view;
    }

    /**
     * Handles the selection of the circuit based on the input provided by the user.
     * Converts the input to an integer and returns the path of the corresponding circuit.
     * In case of invalid input, displays an error message and returns {@code null}.
     *
     * @param input The user input, which represents the number of the desired circuit.
     * @return The path of the selected circuit, or {@code null} if the selection is invalid.
     */
    public String handleCircuitSelection(String input) {
        try {
            int choice = Integer.parseInt(input);
            return switch (choice) {
                case 1 -> CLIConfig.CIRCUITS_DIR.resolve("circuit1.txt").toString();
                case 2 -> CLIConfig.CIRCUITS_DIR.resolve("circuit2.txt").toString();
                case 3 -> CLIConfig.CIRCUITS_DIR.resolve("circuit3.txt").toString();
                default -> {
                    view.showError("Scelta non valida. Inserisci un numero tra 1 e 3.");
                    yield null;
                }
            };
        } catch (NumberFormatException e) {
            view.showError("Input non valido. Inserisci un numero tra 1 e 3.");
            return null;
        }
    }
}