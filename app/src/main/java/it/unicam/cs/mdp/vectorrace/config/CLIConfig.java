package it.unicam.cs.mdp.vectorrace.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides configuration settings for the Command Line Interface (CLI) version of the Vector Race application.
 * This class holds constants and paths specifically used by the CLI.
 */
public class CLIConfig {
    /**
     * The directory where circuit files are stored.
     */
    public static final Path CIRCUITS_DIR = Paths.get("src/main/resources/circuits");
    /**
     * The file containing player configurations for the CLI mode.
     */
    public static final Path PLAYERS_FILE = Paths.get("src/main/resources/players/playersCLI.txt");

    private CLIConfig() {
        // Private constructor to prevent instantiation
    }
}