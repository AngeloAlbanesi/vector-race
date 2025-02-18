package it.unicam.cs.mdp.vectorrace.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configurazioni per la GUI dell'applicazione.
 * Contiene costanti e path utilizzati dall'interfaccia grafica.
 */
public class GUIConfig {
    public static final Path PLAYERS_FILE = Paths.get("src/main/resources/players/playersGUI.txt");

    private GUIConfig() {
        // Costruttore privato per evitare istanziazione
    }
}
