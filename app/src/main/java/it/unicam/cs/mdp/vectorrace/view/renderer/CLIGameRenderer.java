package it.unicam.cs.mdp.vectorrace.view.renderer;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.game.GameState;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

/**
 * Implementation of the game renderer for the CLI.
 * It is responsible for converting the game state into a textual representation.
 */
public class CLIGameRenderer implements IGameRenderer {

    private static final String WALL_CELL = "█";
    private static final String START_CELL = "S";
    private static final String FINISH_CELL = "F";
    private static final String EMPTY_CELL = "·";

    @Override
    public String renderGame(GameState gameState) {
        if (gameState == null) return "";

        Track track = gameState.getTrack();
        StringBuilder builder = new StringBuilder();

        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                Position currentPos = new Position(x, y);
                builder.append(getCellRepresentation(currentPos, track, gameState));
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private String getCellRepresentation(Position pos, Track track, GameState gameState) {
        // Firt check if there is a player at the position
        for (Player player : gameState.getPlayers()) {
            if (player.getPosition().equals(pos)) {
                return player.getName().substring(player.getName().length() - 1);
            }
        }

        // Otherwise, determine the cell type
        CellType cellType = track.getCell(pos.getX(), pos.getY());
        return switch (cellType) {
            case WALL -> WALL_CELL;
            case START -> START_CELL;
            case FINISH -> FINISH_CELL;
            case ROAD, CHECKPOINT -> EMPTY_CELL;
        };
    }
}