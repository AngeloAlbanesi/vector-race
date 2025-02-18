package it.unicam.cs.mdp.vectorrace.view.gui;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;
import it.unicam.cs.mdp.vectorrace.view.renderer.IGUIRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Set;

/**
 * JavaFX implementation of the graphical game renderer.
 * Manages the rendering of the track and players on a canvas.
 */
public class GUIRenderer implements IGUIRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final int cellSize;

    /**
     * Constructor for the {@code GUIRenderer} class.
     *
     * @param canvas   The canvas to render the game on.
     * @param cellSize The size of the cells in the game.
     */
    public GUIRenderer(Canvas canvas, int cellSize) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.cellSize = cellSize;
    }

    @Override
    public void render(GameState state, Set<Position> validMoves) {
        drawTrack(state.getTrack(), validMoves);
        drawPlayers(state.getPlayers());
    }

    /**
     * Draws the track with the valid cells highlighted.
     *
     * @param track      The game track.
     * @param validMoves The set of valid moves for the current player.
     */
    private void drawTrack(Track track, Set<Position> validMoves) {
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                drawCell(x, y, track.getCell(x, y), validMoves.contains(new Position(x, y)));
            }
        }
    }

    /**
     * Draws a single cell of the track.
     *
     * @param x          The x coordinate of the cell.
     * @param y          The y coordinate of the cell.
     * @param cellType   The type of the cell.
     * @param isValidMove Whether the cell is a valid move for the current player.
     */
    private void drawCell(int x, int y, CellType cellType, boolean isValidMove) {
        Color fillColor;

        if (isValidMove) {
            fillColor = GUIConstants.VALID_MOVE_COLOR;
        } else {
            fillColor = switch (cellType) {
                case WALL -> GUIConstants.WALL_COLOR;
                case ROAD, CHECKPOINT -> GUIConstants.ROAD_COLOR;
                case START -> GUIConstants.START_COLOR;
                case FINISH -> GUIConstants.FINISH_COLOR;
                default -> GUIConstants.DEFAULT_COLOR;
            };
        }

        gc.setFill(fillColor);
        gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x * cellSize, y * cellSize, cellSize, cellSize);
    }

    /**
     * Draws all players on the map.
     *
     * @param players The players to draw.
     */
    private void drawPlayers(Iterable<Player> players) {
        for (Player player : players) {
            drawPlayer(player);
        }
    }

    /**
     * Draws a single player.
     *
     * @param player The player to draw.
     */
    private void drawPlayer(Player player) {
        Position pos = player.getPosition();
        Color playerColor = Color.rgb(
            player.getColor().getRed(),
            player.getColor().getGreen(),
            player.getColor().getBlue()
        );

        gc.setFill(playerColor);
        gc.fillOval(
            pos.getX() * cellSize + GUIConstants.PLAYER_MARGIN,
            pos.getY() * cellSize + GUIConstants.PLAYER_MARGIN,
            cellSize - GUIConstants.PLAYER_SIZE_REDUCTION,
            cellSize - GUIConstants.PLAYER_SIZE_REDUCTION
        );
    }
}