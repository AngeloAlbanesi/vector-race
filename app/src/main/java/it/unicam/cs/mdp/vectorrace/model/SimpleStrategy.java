/*
 *   Copyright (c) 2025 Angelo Albanesi
 *   All rights reserved.
 */
package it.unicam.cs.mdp.vectorrace.model;

import java.util.*;

/**
 * Strategia che utilizza A* per trovare il percorso ottimale attraverso i
 * checkpoint.
 */
public class SimpleStrategy implements AIStrategy {
    private static final Vector[] ACCELERATIONS = {
            new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1),
            new Vector(0, -1), new Vector(0, 0), new Vector(0, 1),
            new Vector(1, -1), new Vector(1, 0), new Vector(1, 1)
    };

    private Map<Integer, Set<Position>> checkpointsByLevel = new HashMap<>();
    private Map<String, Position> playerTargets = new HashMap<>();
    private Map<String, Integer> stuckCounter = new HashMap<>();
    private Map<Position, String> checkpointReservations = new HashMap<>();
    private Set<Position> occupiedPositions = new HashSet<>();
    private Map<String, Set<Position>> playerHistory = new HashMap<>();
    private Map<String, Integer> loopCounter = new HashMap<>();
    private Map<String, Vector> lastDirection = new HashMap<>();
    private Map<String, Set<Position>> passedCheckpoints = new HashMap<>();
    private Random random = new Random();

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        String playerName = player.getName();
        Vector currentVelocity = player.getVelocity();
        Position currentPosition = player.getPosition();

        Set<Position> history = playerHistory.computeIfAbsent(playerName, k -> new HashSet<>());
        history.add(currentPosition);

        if (history.size() > 5) {
            int loopCount = loopCounter.getOrDefault(playerName, 0);
            if (loopCount >= 3) {
                history.clear();
                loopCounter.put(playerName, 0);
                playerTargets.remove(playerName);
                lastDirection.remove(playerName);
            } else {
                loopCounter.put(playerName, loopCount + 1);
            }
        }

        if (currentVelocity.getDx() == 0 && currentVelocity.getDy() == 0) {
            int stuck = stuckCounter.getOrDefault(playerName, 0) + 1;
            stuckCounter.put(playerName, stuck);

            if (stuck >= 3) {
                history.clear();
                playerTargets.remove(playerName);
                lastDirection.remove(playerName);
                stuckCounter.put(playerName, 0);
                return new Vector(random.nextInt(3) - 1, random.nextInt(3) - 1);
            }
        } else {
            stuckCounter.put(playerName, 0);
            if (currentVelocity.getDx() != 0 || currentVelocity.getDy() != 0) {
                lastDirection.put(playerName, currentVelocity);
            }
        }

        updateCheckpointMap(gameState.getTrack());
        updateOccupiedPositions(gameState);
        cleanupReservations(gameState);

        Position target = findNextTarget(player, gameState);
        if (target == null) {
            return new Vector(0, 0);
        }

        checkpointReservations.put(target, playerName);
        playerTargets.put(playerName, target);

        VectorRacePathState bestPath = findPath(player, gameState, target);
        if (bestPath != null && bestPath.getParent() != null) {
            Vector resultingVelocity = bestPath.getVelocity();
            Vector acceleration = resultingVelocity.subtract(currentVelocity);

            if (acceleration != null && Math.abs(acceleration.getDx()) <= 1 && Math.abs(acceleration.getDy()) <= 1) {
                Position nextPos = currentPosition.move(currentVelocity.add(acceleration));
                if (!isPositionOccupied(nextPos, gameState, player)) {
                    checkCrossedCheckpoint(player, currentPosition, nextPos, gameState);
                    return acceleration;
                }
            }
        }

        Vector simpleAcc = getSimpleAcceleration(currentPosition, target, player, gameState);
        Position nextPos = currentPosition.move(currentVelocity.add(simpleAcc));
        checkCrossedCheckpoint(player, currentPosition, nextPos, gameState);
        return simpleAcc;
    }

    private void cleanupReservations(GameState gameState) {
        Iterator<Map.Entry<Position, String>> it = checkpointReservations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Position, String> entry = it.next();
            Position checkpoint = entry.getKey();
            String playerName = entry.getValue();

            boolean remove = true;
            for (Player p : gameState.getPlayers()) {
                if (p.getName().equals(playerName)) {
                    int checkpointLevel = gameState.getTrack().getCheckpointNumber(checkpoint);
                    if (p.getNextCheckpointIndex() == checkpointLevel) {
                        remove = false;
                    }
                    break;
                }
            }

            if (remove) {
                it.remove();
                playerTargets.remove(playerName);
            }
        }
    }

    private Position findNextTarget(Player player, GameState gameState) {
        int currentCheckpointIndex = player.getNextCheckpointIndex();
        Set<Position> checkpoints = checkpointsByLevel.get(currentCheckpointIndex);
        Set<Position> passed = passedCheckpoints.computeIfAbsent(player.getName(), k -> new HashSet<>());

        if (checkpoints == null || checkpoints.isEmpty()) {
            return null;
        }

        Position currentTarget = playerTargets.get(player.getName());
        if (currentTarget != null && checkpoints.contains(currentTarget) &&
                !passed.contains(currentTarget)) {
            return currentTarget;
        }

        Position bestCheckpoint = null;
        double bestScore = Double.MAX_VALUE;

        Vector currentVelocity = player.getVelocity();
        boolean hasDirection = currentVelocity.getDx() != 0 || currentVelocity.getDy() != 0;
        Vector lastDir = lastDirection.get(player.getName());

        for (Position cp : checkpoints) {
            if (passed.contains(cp)) {
                continue;
            }

            int dx = cp.getX() - player.getPosition().getX();
            int dy = cp.getY() - player.getPosition().getY();

            if (hasDirection) {
                if ((currentVelocity.getDx() > 0 && dx < 0) ||
                        (currentVelocity.getDx() < 0 && dx > 0) ||
                        (currentVelocity.getDy() > 0 && dy < 0) ||
                        (currentVelocity.getDy() < 0 && dy > 0)) {
                    continue;
                }
            }

            double distance = calculateDistance(player.getPosition(), cp);

            if (lastDir != null) {
                double directionAlignment = Math.abs(Math.atan2(dy, dx) -
                        Math.atan2(lastDir.getDy(), lastDir.getDx()));
                distance += directionAlignment * 30;
            }

            boolean isReachable = isCheckpointReachable(player, cp, gameState);
            if (!isReachable) {
                continue;
            }

            if (checkpointReservations.containsKey(cp) &&
                    !checkpointReservations.get(cp).equals(player.getName())) {
                distance += 20;
            }

            if (distance < bestScore) {
                bestScore = distance;
                bestCheckpoint = cp;
            }
        }

        if (bestCheckpoint != null) {
            checkpointReservations.put(bestCheckpoint, player.getName());
            playerTargets.put(player.getName(), bestCheckpoint);
            return bestCheckpoint;
        }

        return null;
    }

    private boolean isCheckpointReachable(Player player, Position checkpoint, GameState gameState) {
        Position current = player.getPosition();
        Vector velocity = player.getVelocity();

        int dx = checkpoint.getX() - current.getX();
        int dy = checkpoint.getY() - current.getY();

        if (velocity.getDx() != 0 || velocity.getDy() != 0) {
            if ((velocity.getDx() > 0 && dx < 0) ||
                    (velocity.getDx() < 0 && dx > 0) ||
                    (velocity.getDy() > 0 && dy < 0) ||
                    (velocity.getDy() < 0 && dy > 0)) {
                return false;
            }
        }

        return player.isPathClear(current, checkpoint, gameState.getTrack(), gameState);
    }

    private double calculateDistance(Position a, Position b) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void updateOccupiedPositions(GameState gameState) {
        occupiedPositions.clear();
        for (Player p : gameState.getPlayers()) {
            occupiedPositions.add(p.getPosition());
        }
    }

    private void updateCheckpointMap(Track track) {
        checkpointsByLevel.clear();
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.getCell(x, y) == CellType.CHECKPOINT) {
                    int level = track.getCheckpointNumber(new Position(x, y));
                    checkpointsByLevel.computeIfAbsent(level, k -> new HashSet<>())
                            .add(new Position(x, y));
                }
            }
        }
    }

    private boolean isPositionOccupied(Position pos, GameState gameState, Player currentPlayer) {
        for (Player p : gameState.getPlayers()) {
            if (!p.getName().equals(currentPlayer.getName())) {
                if (p.getPosition().equals(pos)) {
                    return true;
                }
                Position nextPos = p.getPosition().move(p.getVelocity());
                if (nextPos.equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Vector getSimpleAcceleration(Position current, Position target, Player player, GameState gameState) {
        Vector currentVelocity = player.getVelocity();
        int dx = target.getX() - current.getX();
        int dy = target.getY() - current.getY();
        Vector lastDir = lastDirection.get(player.getName());

        if (currentVelocity.getDx() == 0 && currentVelocity.getDy() == 0) {
            Vector bestAcc = null;
            double bestScore = Double.MAX_VALUE;

            for (Vector acc : ACCELERATIONS) {
                Position newPos = current.move(acc);
                if (player.isPathClear(current, newPos, gameState.getTrack(), gameState) &&
                        !isPositionOccupied(newPos, gameState, player)) {
                    double score = Math.abs(acc.getDx() - Integer.compare(dx, 0)) +
                            Math.abs(acc.getDy() - Integer.compare(dy, 0));

                    if (lastDir != null) {
                        if (acc.getDx() * lastDir.getDx() > 0)
                            score -= 2;
                        if (acc.getDy() * lastDir.getDy() > 0)
                            score -= 2;
                    }

                    if (acc.getDx() * dx > 0)
                        score -= 3;
                    if (acc.getDy() * dy > 0)
                        score -= 3;

                    if (score < bestScore) {
                        bestScore = score;
                        bestAcc = acc;
                    }
                }
            }

            return bestAcc != null ? bestAcc : new Vector(0, 0);
        }

        if ((currentVelocity.getDx() * dx >= 0 && currentVelocity.getDy() * dy >= 0) &&
                Math.abs(currentVelocity.getDx()) <= 2 && Math.abs(currentVelocity.getDy()) <= 2) {
            return new Vector(0, 0);
        }

        int targetDx = Integer.compare(dx, 0);
        int targetDy = Integer.compare(dy, 0);

        Vector proposedAcc = new Vector(targetDx, targetDy);
        Position newPos = current.move(currentVelocity.add(proposedAcc));

        if (player.isPathClear(current, newPos, gameState.getTrack(), gameState) &&
                !isPositionOccupied(newPos, gameState, player)) {
            return proposedAcc;
        }

        return new Vector(
                -Integer.compare(currentVelocity.getDx(), 0),
                -Integer.compare(currentVelocity.getDy(), 0));
    }

    private VectorRacePathState findPath(Player player, GameState gameState, Position target) {
        PriorityQueue<VectorRacePathState> openSet = new PriorityQueue<>();
        Set<VectorRacePathState> closedSet = new HashSet<>();

        VectorRacePathState start = new VectorRacePathState(player.getPosition(), player.getVelocity(), null);
        start.setG(0);
        start.setH(calculateDistance(player.getPosition(), target));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            VectorRacePathState current = openSet.poll();
            if (current.getPosition().equals(target)) {
                return current;
            }

            closedSet.add(current);

            for (Vector acc : ACCELERATIONS) {
                Vector newVelocity = current.getVelocity().add(acc);
                Position newPosition = current.getPosition().move(newVelocity);

                if (!player.isPathClear(current.getPosition(), newPosition, gameState.getTrack(), gameState)) {
                    continue;
                }

                VectorRacePathState neighbor = new VectorRacePathState(newPosition, newVelocity, current);
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double tentativeG = current.getG() + 1;

                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else if (tentativeG >= neighbor.getG()) {
                    continue;
                }

                neighbor.setG(tentativeG);
                neighbor.setH(calculateDistance(newPosition, target));
            }
        }

        return null;
    }

    private void checkCrossedCheckpoint(Player player, Position oldPos, Position newPos, GameState gameState) {
        Track track = gameState.getTrack();
        int x1 = oldPos.getX(), y1 = oldPos.getY();
        int x2 = newPos.getX(), y2 = newPos.getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1, y = y1;
        Set<Position> passed = passedCheckpoints.computeIfAbsent(player.getName(), k -> new HashSet<>());

        while (true) {
            Position currentPos = new Position(x, y);
            if (track.getCell(x, y) == CellType.CHECKPOINT) {
                int checkpointNum = track.getCheckpointNumber(currentPos);
                if (checkpointNum == player.getNextCheckpointIndex()) {
                    passed.add(currentPos);
                    player.incrementCheckpointIndex();
                    checkpointReservations.remove(currentPos);
                    playerTargets.remove(player.getName());
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

    public int getStuckCounter(String playerName) {
        return stuckCounter.getOrDefault(playerName, 0);
    }
}