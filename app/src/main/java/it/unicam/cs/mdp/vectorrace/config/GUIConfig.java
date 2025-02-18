package it.unicam.cs.mdp.vectorrace.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides configuration settings for the Graphical User Interface (GUI) version of the Vector Race application.
 * This class holds constants and paths specifically used by the GUI.
 */
public class GUIConfig {
    /**
     * The file containing player configurations for the GUI mode.
     */
    public static final Path PLAYERS_FILE = Paths.get("src/main/resources/players/playersGUI.txt");

    private GUIConfig() {
        // Private constructor to prevent instantiation
    }
}
