/*
 *   Copyright (c) 2025 ...
 */
package it.unicam.cs.mdp.vectorrace.model;

import java.util.*;

/**
 * Strategia che utilizza una variante di A* per trovare il percorso ottimale
 * attraverso i checkpoint, evitando movimenti retrogradi e ritorni su gruppi già superati.
 * Include una logica di fallback, un'euristica angolare per l'A*, e un
 * getSimpleAcceleration(...) con la firma corretta.
 */
public class SimpleStrategy implements AIStrategy {

    /**
     * Possibili accelerazioni (9 direzioni).
     */
    private static final Vector[] ACCELERATIONS = {
            new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1),
            new Vector(0, -1),  new Vector(0, 0),  new Vector(0, 1),
            new Vector(1, -1),  new Vector(1, 0),  new Vector(1, 1)
    };

    /**
     * Se lo stesso bot riprova la stessa accelerazione per più di FALLBACK_THRESHOLD volte
     * e fallisce (traiettoria bloccata) ogni volta, forziamo un fallback casuale.
     */
    private static final int FALLBACK_THRESHOLD = 2;

    private Map<String, Vector> lastTriedAcceleration = new HashMap<>();
    private Map<String, Integer> failedAttemptsCounter = new HashMap<>();

    // Mappa dei checkpoint per livello (static: non cambia dopo l'inizializzazione)
    private Map<Integer, Set<Position>> checkpointsByLevel = new HashMap<>();
    private boolean checkpointsMapInitialized = false;

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

        // Gestione dello storico per verificare eventuali loop
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

        // Gestione del “blocco” (stuck): se il giocatore è fermo per troppi turni, si applica una spinta casuale
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
            if (!currentVelocity.isZero()) {
                lastDirection.put(playerName, currentVelocity);
            }
        }

        // Inizializza la mappa dei checkpoint (sola volta) perché il tracciato è statico
        if (!checkpointsMapInitialized) {
            updateCheckpointMap(gameState.getTrack());
            checkpointsMapInitialized = true;
        }
        updateOccupiedPositions(gameState);
        cleanupReservations(gameState);

        // Seleziona (o aggiorna) il target corrente
        Position target = findNextTarget(player, gameState);
        if (target == null) {
            // Se non ci sono più checkpoint, prova con il traguardo
            target = findFinishCell(gameState);
            if (target == null) {
                return new Vector(0, 0);
            }
        }
        checkpointReservations.put(target, playerName);
        playerTargets.put(playerName, target);

        // 1) Tentativo con A*
        VectorRacePathState bestPath = findPath(player, gameState, target);
        if (bestPath != null && bestPath.getParent() != null) {
            Vector chosenAcc = bestPath.getVelocity().subtract(currentVelocity);
            if (isMoveValid(player, chosenAcc, gameState)) {
                resetFailCount(playerName, chosenAcc);
                // Aggiornamento checkpoint: la strategia gestisce l'update quando la traiettoria è eseguita
                Position nextPos = currentPosition.move(currentVelocity.add(chosenAcc));
                checkCrossedCheckpoint(player, currentPosition, nextPos, gameState);
                return chosenAcc;
            } else {
                incrementFailCount(playerName, chosenAcc);
            }
        }

        // 2) Se A* non produce una mossa valida, usa getSimpleAcceleration
        Vector simpleAcc = getSimpleAcceleration(currentPosition, target, player, gameState);
        if (isMoveValid(player, simpleAcc, gameState)) {
            resetFailCount(playerName, simpleAcc);
            Position nextPos = currentPosition.move(currentVelocity.add(simpleAcc));
            checkCrossedCheckpoint(player, currentPosition, nextPos, gameState);
            return simpleAcc;
        } else {
            incrementFailCount(playerName, simpleAcc);
            // 3) Fallback: se si supera la soglia di fallimenti, scegli un'accelerazione casuale valida
            int fails = failedAttemptsCounter.getOrDefault(playerName, 0);
            if (fails >= FALLBACK_THRESHOLD) {
                Vector fallback = chooseRandomValidAcceleration(player, gameState);
                if (fallback != null) {
                    resetFailCount(playerName, fallback);
                    Position nextPos = currentPosition.move(currentVelocity.add(fallback));
                    checkCrossedCheckpoint(player, currentPosition, nextPos, gameState);
                    return fallback;
                }
            }
        }
        // 4) Se tutto fallisce, resta fermo
        return new Vector(0, 0);
    }

    /**
     * Implementazione "semplice" dell'accelerazione se A* non fornisce una mossa valida.
     */
    private Vector getSimpleAcceleration(Position current,
                                        Position target,
                                        Player player,
                                        GameState gameState) {
        Vector currentVelocity = player.getVelocity();
        int dx = target.getX() - current.getX();
        int dy = target.getY() - current.getY();
        Vector lastDir = lastDirection.get(player.getName());

        if (currentVelocity.isZero()) {
            Vector bestAcc = null;
            double bestScore = Double.MAX_VALUE;
            for (Vector acc : ACCELERATIONS) {
                Position newPos = current.move(acc);
                if (player.isPathClear(current, newPos, gameState.getTrack(), gameState)
                        && !isPositionOccupied(newPos, gameState, player)) {
                    double score = Math.abs(acc.getDx() - Integer.compare(dx, 0))
                                   + Math.abs(acc.getDy() - Integer.compare(dy, 0));
                    if (lastDir != null) {
                        if (acc.getDx() * lastDir.getDx() > 0) score -= 1.5;
                        if (acc.getDy() * lastDir.getDy() > 0) score -= 1.5;
                    }
                    if (acc.getDx() * dx > 0) score -= 2;
                    if (acc.getDy() * dy > 0) score -= 2;
                    if (score < bestScore) {
                        bestScore = score;
                        bestAcc = acc;
                    }
                }
            }
            return bestAcc != null ? bestAcc : new Vector(0, 0);
        }

        if ((currentVelocity.getDx() * dx >= 0 && currentVelocity.getDy() * dy >= 0)
            && Math.abs(currentVelocity.getDx()) <= 2
            && Math.abs(currentVelocity.getDy()) <= 2) {
            return new Vector(0, 0);
        }

        int targetDx = Integer.compare(dx, 0);
        int targetDy = Integer.compare(dy, 0);
        Vector proposedAcc = new Vector(targetDx, targetDy);
        Position newPos = current.move(currentVelocity.add(proposedAcc));
        if (player.isPathClear(current, newPos, gameState.getTrack(), gameState)
            && !isPositionOccupied(newPos, gameState, player)) {
            return proposedAcc;
        }
        return new Vector(-Integer.compare(currentVelocity.getDx(), 0),
                          -Integer.compare(currentVelocity.getDy(), 0));
    }

    /**
     * Seleziona il prossimo checkpoint target. <br>
     * In questa versione si considerano **soltanto** i checkpoint il cui numero
     * corrisponde esattamente al valore atteso (player.getNextCheckpointIndex()).
     */
    private Position findNextTarget(Player player, GameState gameState) {
        int currentCheckpointIndex = player.getNextCheckpointIndex();
        Track track = gameState.getTrack();

        // Se non sono presenti checkpoint per il gruppo corrente, restituisci null
        if (!checkpointsByLevel.containsKey(currentCheckpointIndex)) {
            return null;
        }

        Set<Position> checkpoints = checkpointsByLevel.get(currentCheckpointIndex);
        Set<Position> passed = passedCheckpoints.computeIfAbsent(player.getName(), k -> new HashSet<>());
        Position currentTarget = playerTargets.get(player.getName());

        if (currentTarget != null && checkpoints.contains(currentTarget) && !passed.contains(currentTarget)) {
            return currentTarget;
        }

        Position bestCheckpoint = null;
        double bestScore = Double.MAX_VALUE;
        Vector lastDir = lastDirection.get(player.getName());
        Vector currentVelocity = player.getVelocity();

        for (Position cp : checkpoints) {
            // Escludi i checkpoint già passati
            if (passed.contains(cp)) {
                continue;
            }
            // **Forza la scelta esclusiva del checkpoint del gruppo corrente**
            int checkpointNum = track.getCheckpointNumber(cp);
            if (checkpointNum != currentCheckpointIndex) {
                continue;
            }

            double distance = calculateDistance(player.getPosition(), cp);
            if (lastDir != null) {
                int dx = cp.getX() - player.getPosition().getX();
                int dy = cp.getY() - player.getPosition().getY();
                double angleDiff = Math.abs(Math.atan2(dy, dx) - Math.atan2(lastDir.getDy(), lastDir.getDx()));
                distance += angleDiff * 20;
            }
            if (!isCheckpointReachable(player, cp, gameState)) {
                continue;
            }
            if (checkpointReservations.containsKey(cp) && !checkpointReservations.get(cp).equals(player.getName())) {
                distance += 40;
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

    /**
     * Verifica se una mossa è valida, ovvero se il percorso risultante è libero.
     */
    private boolean isMoveValid(Player player, Vector acceleration, GameState gameState) {
        Position currentPos = player.getPosition();
        Vector newVelocity = player.getVelocity().add(acceleration);
        Position nextPos = currentPos.move(newVelocity);
        return player.isPathClear(currentPos, nextPos, gameState.getTrack(), gameState)
                && !isPositionOccupied(nextPos, gameState, player);
    }

    private Vector chooseRandomValidAcceleration(Player player, GameState gameState) {
        List<Vector> candidates = new ArrayList<>(Arrays.asList(ACCELERATIONS));
        Collections.shuffle(candidates);
        for (Vector acc : candidates) {
            if (isMoveValid(player, acc, gameState)) {
                return acc;
            }
        }
        return null;
    }

    private void incrementFailCount(String playerName, Vector acceleration) {
        Vector lastAcc = lastTriedAcceleration.get(playerName);
        if (lastAcc != null && lastAcc.equals(acceleration)) {
            int fails = failedAttemptsCounter.getOrDefault(playerName, 0) + 1;
            failedAttemptsCounter.put(playerName, fails);
        } else {
            failedAttemptsCounter.put(playerName, 1);
            lastTriedAcceleration.put(playerName, acceleration);
        }
    }

    private void resetFailCount(String playerName, Vector newAcceleration) {
        lastTriedAcceleration.put(playerName, newAcceleration);
        failedAttemptsCounter.put(playerName, 0);
    }

    /**
     * Rimuove dalle prenotazioni i checkpoint non più rilevanti.
     */
    private void cleanupReservations(GameState gameState) {
        Iterator<Map.Entry<Position, String>> it = checkpointReservations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Position, String> entry = it.next();
            Position checkpoint = entry.getKey();
            String pName = entry.getValue();
            boolean remove = true;
            for (Player p : gameState.getPlayers()) {
                if (p.getName().equals(pName)) {
                    int checkpointLevel = gameState.getTrack().getCheckpointNumber(checkpoint);
                    if (p.getNextCheckpointIndex() == checkpointLevel) {
                        remove = false;
                    }
                    break;
                }
            }
            if (remove) {
                it.remove();
                playerTargets.remove(pName);
            }
        }
    }

    /**
     * Restituisce la cella FINISH (traguardo) del tracciato.
     */
    private Position findFinishCell(GameState gameState) {
        Track track = gameState.getTrack();
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (track.getCell(x, y) == CellType.FINISH) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    /**
     * Verifica se un checkpoint è raggiungibile.
     */
    private boolean isCheckpointReachable(Player player, Position checkpoint, GameState gameState) {
        Position current = player.getPosition();
        Vector velocity = player.getVelocity();
        int dx = checkpoint.getX() - current.getX();
        int dy = checkpoint.getY() - current.getY();
        if (!velocity.isZero()) {
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

    /**
     * Calcola la mappa dei checkpoint raggruppati per livello. Dal momento che il tracciato
     * è statico, questa operazione viene eseguita una sola volta.
     */
    private void updateCheckpointMap(Track track) {
        // Non cancelliamo la mappa se già inizializzata
        if (!checkpointsByLevel.isEmpty()) {
            return;
        }
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

    private void updateOccupiedPositions(GameState gameState) {
        occupiedPositions.clear();
        for (Player p : gameState.getPlayers()) {
            occupiedPositions.add(p.getPosition());
        }
    }

    /**
     * Algoritmo A* per trovare un percorso verso il target, utilizzando un'euristica
     * che penalizza la differenza angolare.
     */
    private VectorRacePathState findPath(Player player, GameState gameState, Position target) {
        PriorityQueue<VectorRacePathState> openSet = new PriorityQueue<>();
        Set<VectorRacePathState> closedSet = new HashSet<>();

        VectorRacePathState start = new VectorRacePathState(player.getPosition(), player.getVelocity(), null);
        start.setG(0);
        start.setH(computeHeuristic(player.getPosition(), player.getVelocity(), target));
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
                if (!openSet.contains(neighbor) || tentativeG < neighbor.getG()) {
                    neighbor.setG(tentativeG);
                    neighbor.setH(computeHeuristic(newPosition, newVelocity, target));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Calcola l'euristica come somma della distanza euclidea e di una penalità angolare.
     */
    private double computeHeuristic(Position pos, Vector velocity, Position target) {
        double dist = calculateDistance(pos, target);
        double anglePenalty = 0.0;
        if (!velocity.isZero()) {
            double angleVel = Math.atan2(velocity.getDy(), velocity.getDx());
            double angleCheckpoint = Math.atan2(target.getY() - pos.getY(), target.getX() - pos.getX());
            double diff = Math.abs(angleVel - angleCheckpoint);
            if (diff > Math.PI) {
                diff = 2 * Math.PI - diff;
            }
            anglePenalty = diff * 5.0;
        }
        return dist + anglePenalty;
    }

    /**
     * Metodo che controlla se lungo il percorso (tratto con l'algoritmo Bresenham)
     * il giocatore ha superato il checkpoint atteso. In tal caso, aggiorna l'indice.
     */
    private void checkCrossedCheckpoint(Player player, Position oldPos, Position newPos, GameState gameState) {
        Track track = gameState.getTrack();
        int x1 = oldPos.getX(), y1 = oldPos.getY();
        int x2 = newPos.getX(), y2 = newPos.getY();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
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
                    // Avanza al checkpoint successivo
                    player.incrementCheckpointIndex();
                    removeAllCheckpointReservationsForIndex(currentIndex, player.getName(), track);
                    playerTargets.remove(player.getName());
                    return;
                }
            }
            if (x == x2 && y == y2) {
                break;
            }
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

    /**
     * Rimuove dalle prenotazioni tutti i checkpoint appartenenti al gruppo specificato per il giocatore.
     */
    private void removeAllCheckpointReservationsForIndex(int checkpointIndex, String playerName, Track track) {
        Iterator<Map.Entry<Position, String>> it = checkpointReservations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Position, String> entry = it.next();
            Position cpPos = entry.getKey();
            String cpPlayer = entry.getValue();
            if (cpPlayer.equals(playerName)) {
                if (track.getCell(cpPos.getX(), cpPos.getY()) == CellType.CHECKPOINT) {
                    int cpLevel = track.getCheckpointNumber(cpPos);
                    if (cpLevel == checkpointIndex) {
                        it.remove();
                    }
                }
            }
        }
    }

    private boolean isPositionOccupied(Position position, GameState gameState, Player currentPlayer) {
        for (Player otherPlayer : gameState.getPlayers()) {
            if (!otherPlayer.equals(currentPlayer) && otherPlayer.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    public int getStuckCounter(String playerName) {
        return stuckCounter.getOrDefault(playerName, 0);
    }
}
