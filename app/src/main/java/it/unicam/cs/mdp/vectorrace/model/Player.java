package it.unicam.cs.mdp.vectorrace.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe astratta che rappresenta un giocatore (umano o bot).
 */
public abstract class Player {

    protected final String name;
    protected final Color color;
    protected Position position;
    protected Vector velocity;
    protected final List<Position> movementHistory; // Storico dei movimenti
    protected int nextCheckpointIndex = 1; // Checkpoint successivo
    protected final Set<Position> visitedCheckpoints = new HashSet<>(); // Per tracciare i checkpoint attraversati

    /**
     * Costruttore.
     *
     * @param name          nome del giocatore
     * @param color         colore (per visualizzazione)
     * @param startPosition posizione iniziale
     */
    public Player(String name, Color color, Position startPosition) {
        this.name = name;
        this.color = color;
        this.position = startPosition;
        this.velocity = new Vector(0, 0);
        this.movementHistory = new ArrayList<>();
        this.movementHistory.add(startPosition);
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public List<Position> getMovementHistory() {
        return movementHistory; // Restituisci una copia per sicurezza
    }

    public int getNextCheckpointIndex() {
        return nextCheckpointIndex;
    }

    public void incrementCheckpointIndex() {
        nextCheckpointIndex++;
    }

    /**
     * Calcola la prossima accelerazione. Le componenti devono essere
     * nell’intervallo [-1, 1].
     *
     * @param gameState stato corrente del gioco
     * @return vettore di accelerazione scelto
     */
    public abstract Vector getNextAcceleration(GameState gameState);

    /**
     * Aggiorna la velocità e la posizione del giocatore, registrando il
     * movimento e controllando i checkpoint attraversati.
     *
     * @param acceleration accelerazione scelta
     * @param gameState    stato del gioco corrente
     * @return true se il movimento è valido ed è stato eseguito, false altrimenti
     */
    public boolean move(Vector acceleration, GameState gameState) {
        Vector newVelocity = velocity.add(acceleration);
        Position newPosition = position.move(newVelocity);
        Track track = gameState.getTrack();
        boolean isStuck = velocity.getDx() == 0 && velocity.getDy() == 0;

        // Logged per debug
        if (this instanceof BotPlayer) {
            BotPlayer bot = (BotPlayer) this;
            bot.logDebug("Tentativo movimento: Posizione attuale=" + position +
                    ", Nuova posizione=" + newPosition +
                    ", Nuova velocità=" + newVelocity);
        }

        // Verifica confini
        if (newPosition.getX() < 0 || newPosition.getX() >= track.getWidth() ||
                newPosition.getY() < 0 || newPosition.getY() >= track.getHeight()) {
            if (this instanceof BotPlayer) {
                ((BotPlayer) this).logDebug("Movimento rifiutato: fuori dai confini");
            }
            return false;
        }

        // Meccanismo di emergenza: se il bot è bloccato e ha provato più volte,
        // ignora completamente le collisioni e permetti il movimento se non va contro
        // un muro
        if (isStuck && this instanceof BotPlayer) {
            BotPlayer bot = (BotPlayer) this;
            int stuckCount = ((SimpleStrategy) bot.getStrategy()).getStuckCounter(getName());

            if (stuckCount >= 5) {
                // Verifica solo collisione con muri
                if (track.getCell(newPosition.getX(), newPosition.getY()) != CellType.WALL) {
                    bot.logDebug("Attivato movimento di emergenza dopo " + stuckCount + " tentativi");
                    velocity = newVelocity;
                    position = newPosition;
                    movementHistory.add(position);
                    checkCrossedCheckpoints(position, newPosition, track);
                    return true;
                } else {
                    bot.logDebug("Movimento di emergenza bloccato da muro");
                }
            }
        }

        // Verifica collisioni standard
        if (!isPathClear(position, newPosition, track, gameState)) {
            if (this instanceof BotPlayer) {
                BotPlayer bot = (BotPlayer) this;
                bot.logDebug("Movimento rifiutato: percorso non libero");
                // Log dettagli percorso
                bot.logDebug("Dettagli percorso - Start: " + position + ", End: " + newPosition);

                // Verifica e log ostacoli specifici
                int dx = newPosition.getX() - position.getX();
                int dy = newPosition.getY() - position.getY();
                int steps = Math.max(Math.abs(dx), Math.abs(dy));
                float xInc = (float) dx / steps;
                float yInc = (float) dy / steps;

                float x = position.getX();
                float y = position.getY();
                for (int i = 1; i <= steps; i++) {
                    int checkX = Math.round(x + xInc * i);
                    int checkY = Math.round(y + yInc * i);
                    if (track.getCell(checkX, checkY) == CellType.WALL) {
                        bot.logDebug("Trovato muro in: (" + checkX + "," + checkY + ")");
                    }
                    for (Player other : gameState.getPlayers()) {
                        if (!other.getName().equals(getName()) &&
                                other.getPosition().equals(new Position(checkX, checkY))) {
                            bot.logDebug(
                                    "Trovato giocatore " + other.getName() + " in: (" + checkX + "," + checkY + ")");
                        }
                    }
                }
            }
            return false;
        }

        // Esegui il movimento
        velocity = newVelocity;
        position = newPosition;
        movementHistory.add(position);
        checkCrossedCheckpoints(position, newPosition, track);

        if (this instanceof BotPlayer) {
            ((BotPlayer) this).logDebug("Movimento eseguito con successo");
        }

        return true;
    }

    /**
     * Verifica se il percorso è libero da ostacoli e altri giocatori
     */
    protected boolean isPathClear(Position start, Position end, Track track, GameState gameState) {
        // For initial moves (when velocity is 0), ONLY check wall collisions at the
        // destination
        if (velocity.getDx() == 0 && velocity.getDy() == 0) {
            // Check only the destination for walls
            return track.getCell(end.getX(), end.getY()) != CellType.WALL;
        }

        // For non-initial moves, do the full path check
        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0)
            return true;

        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;

        // Create set of occupied cells (excluding start position)
        Set<Position> occupiedCells = new HashSet<>();
        for (Player other : gameState.getPlayers()) {
            if (!other.getName().equals(this.name)) {
                Vector otherVel = other.getVelocity();
                // Only consider stationary players as obstacles
                if (otherVel.getDx() == 0 && otherVel.getDy() == 0) {
                    occupiedCells.add(other.getPosition());
                }
            }
        }

        // Check the path for collisions
        float x = start.getX();
        float y = start.getY();
        for (int i = 0; i <= steps; i++) {
            int currentX = Math.round(x);
            int currentY = Math.round(y);

            // Skip the starting position
            if (!(currentX == start.getX() && currentY == start.getY())) {
                // Check walls
                if (track.getCell(currentX, currentY) == CellType.WALL) {
                    return false;
                }
                // Only check collisions with stationary players
                if (occupiedCells.contains(new Position(currentX, currentY))) {
                    return false;
                }
            }
            x += xIncrement;
            y += yIncrement;
        }

        return true;
    }

    /**
     * Controlla se il giocatore ha attraversato dei checkpoint durante il movimento
     */
    private void checkCrossedCheckpoints(Position start, Position end, Track track) {
        int x1 = start.getX(), y1 = start.getY();
        int x2 = end.getX(), y2 = end.getY();

        // Usa Bresenham per controllare tutte le celle attraversate
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1, y = y1;
        boolean firstCell = true;

        while (true) {
            if (!firstCell) { // Salta la cella di partenza
                Position currentPos = new Position(x, y);
                if (track.getCell(x, y) == CellType.CHECKPOINT) {
                    int checkpointNum = track.getCheckpointNumber(currentPos);
                    if (checkpointNum == nextCheckpointIndex && !hasVisitedCheckpoint(currentPos)) {
                        addVisitedCheckpoint(currentPos);
                        nextCheckpointIndex++;
                    }
                }
            }

            if (x == x2 && y == y2)
                break;

            firstCell = false;
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
     * @deprecated Use move(Vector acceleration, GameState gameState) instead
     */
    @Deprecated
    public void move(Vector acceleration) {
        // Per retrocompatibilità, ma non dovrebbe essere più usato
        velocity = velocity.add(acceleration);
        Position oldPosition = position;
        position = position.move(velocity);
        movementHistory.add(position);
        // Note: i checkpoint non vengono controllati in questa versione
    }

    /**
     * Resetta la velocità del giocatore a (0, 0).
     */
    public void resetVelocity() {
        this.velocity = new Vector(0, 0);
    }

    public void addVisitedCheckpoint(Position checkpoint) {
        visitedCheckpoints.add(checkpoint);
    }

    public boolean hasVisitedCheckpoint(Position checkpoint) {
        return visitedCheckpoints.contains(checkpoint);
    }

    @Override
    public String toString() {
        return name + " in " + position + " con velocità " + velocity;
    }

    public void setNextCheckpointIndex(int nextCheckpointIndex) {
        this.nextCheckpointIndex = nextCheckpointIndex;
    }
}