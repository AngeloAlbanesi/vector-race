package it.unicam.cs.mdp.vectorrace.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configurazioni per la CLI dell'applicazione.
 * Contiene costanti e path utilizzati dall'interfaccia a riga di comando.
 */
public class CLIConfig {
    public static final Path CIRCUITS_DIR = Paths.get("src/main/resources/circuits");
    // Updated players file to use playersCLI.txt for CLI mode
    public static final Path PLAYERS_FILE = Paths.get("src/main/resources/players/playersCLI.txt");

    private CLIConfig() {
        // Costruttore privato per evitare istanziazione
    }
}