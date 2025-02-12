package it.unicam.cs.mdp.vectorrace.view.gui;

import javafx.scene.paint.Color;

/**
 * Costanti utilizzate nell'interfaccia grafica del gioco.
 */
public final class GUIConstants {
    // Dimensioni e proporzioni
    public static final int DEFAULT_CELL_SIZE = 15;
    public static final int MIN_CELL_SIZE = 15;
    public static final int MAX_CELL_SIZE = 30;
    public static final double WINDOW_SCALE_FACTOR = 0.8;
    
    // Colori celle
    public static final Color WALL_COLOR = Color.BLACK;
    public static final Color ROAD_COLOR = Color.LIGHTGRAY;
    public static final Color START_COLOR = Color.LIGHTGREEN;
    public static final Color FINISH_COLOR = Color.LIGHTCORAL;
    public static final Color VALID_MOVE_COLOR = Color.LIGHTGREEN;
    public static final Color DEFAULT_COLOR = Color.WHITE;
    
    // Configurazione controlli
    public static final double MIN_SPEED = 0.1;
    public static final double MAX_SPEED = 2.0;
    public static final double DEFAULT_SPEED = 1.0;
    public static final int CONTROL_SPACING = 10;
    public static final int CONTROL_PADDING = 10;
    
    // Dimensioni giocatore
    public static final int PLAYER_MARGIN = 2;
    public static final int PLAYER_SIZE_REDUCTION = 4;

    private GUIConstants() {
        // Previene l'istanziazione
    }
}