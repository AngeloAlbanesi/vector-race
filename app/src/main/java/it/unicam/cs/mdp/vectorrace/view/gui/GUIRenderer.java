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
 * Implementazione JavaFX del renderer grafico di gioco.
 * Gestisce il rendering del circuito e dei giocatori su un canvas.
 */
public class GUIRenderer implements IGUIRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final int cellSize;

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
     * Disegna il circuito con le celle valide evidenziate.
     */
    private void drawTrack(Track track, Set<Position> validMoves) {
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                drawCell(x, y, track.getCell(x, y), validMoves.contains(new Position(x, y)));
            }
        }
    }

    /**
     * Disegna una singola cella del circuito.
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
     * Disegna tutti i giocatori sulla mappa.
     */
    private void drawPlayers(Iterable<Player> players) {
        for (Player player : players) {
            drawPlayer(player);
        }
    }

    /**
     * Disegna un singolo giocatore.
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