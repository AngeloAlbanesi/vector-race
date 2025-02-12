package it.unicam.cs.mdp.vectorrace.view.cli;

import it.unicam.cs.mdp.vectorrace.config.CLIConfig;
import it.unicam.cs.mdp.vectorrace.view.CLIView;

/**
 * Gestisce la selezione del circuito di gioco tramite interfaccia CLI.
 * Permette all'utente di scegliere tra i circuiti disponibili.
 */
public class CircuitSelector {
    private final CLIView view;

    public CircuitSelector(CLIView view) {
        this.view = view;
    }

    /**
     * Gestisce la selezione del circuito in base all'input dell'utente.
     * 
     * @param input L'input dell'utente (numero del circuito)
     * @return Il path del circuito selezionato o null se la selezione non è valida
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