package it.unicam.cs.mdp.vectorrace.model.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unicam.cs.mdp.vectorrace.model.core.CellType;
import it.unicam.cs.mdp.vectorrace.model.core.Position;
import it.unicam.cs.mdp.vectorrace.model.core.Track;
import it.unicam.cs.mdp.vectorrace.model.players.Player;

public class CheckpointManager {
    private final Map<String, Set<Position>> passedCheckpoints = new HashMap<>();
    private final Map<Position, String> checkpointReservations = new HashMap<>();

    public void checkCrossedCheckpoints(Player player, Position oldPos, Position newPos, Track track) {
        int x1 = oldPos.getX(), y1 = oldPos.getY();
        int x2 = newPos.getX(), y2 = newPos.getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1, y = y1;

        Set<Position> passed = passedCheckpoints.computeIfAbsent(player.getName(), k -> new HashSet<>());
        int currentIndex = player.getNextCheckpointIndex();

        while (true) {
            Position currentPos = new Position(x, y);
            if (track.getCell(x, y) == CellType.CHECKPOINT) {
                int checkpointNum = track.getCheckpointNumber(currentPos);
                if (checkpointNum == currentIndex) {
                    passed.add(currentPos);
                    player.incrementCheckpointIndex();
                    removeCheckpointReservation(currentPos, player.getName());
                }
            }

            if (x == x2 && y == y2)
                break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    public void reserveCheckpoint(Position checkpoint, String playerName) {
        checkpointReservations.put(checkpoint, playerName);
    }

    public boolean isCheckpointReserved(Position checkpoint, String playerName) {
        String reservedBy = checkpointReservations.get(checkpoint);
        return reservedBy != null && !reservedBy.equals(playerName);
    }

    private void removeCheckpointReservation(Position checkpoint, String playerName) {
        if (checkpointReservations.get(checkpoint) != null &&
                checkpointReservations.get(checkpoint).equals(playerName)) {
            checkpointReservations.remove(checkpoint);
        }
    }

    public boolean hasPassedCheckpoint(String playerName, Position checkpoint) {
        return passedCheckpoints.getOrDefault(playerName, new HashSet<>()).contains(checkpoint);
    }
}