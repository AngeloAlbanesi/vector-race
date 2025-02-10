package it.unicam.cs.mdp.vectorrace.model;

import java.util.*;

/**
 * Strategia BFS pura per trovare il percorso con minor numero di mosse (turni).
 * Ogni stato: (Position, Velocity), costo = numero di turni.
 */
public class BFSStrategy implements AIStrategy {

    private final MovementManager movementManager = new MovementManager();
    // Tutte le accelerazioni possibili
    private static final Vector[] ACCELERATIONS = {
        new Vector(-1, -1), new Vector(-1, 0), new Vector(-1, 1),
        new Vector(0, -1),  new Vector(0, 0),  new Vector(0, 1),
        new Vector(1, -1),  new Vector(1, 0),  new Vector(1, 1)
    };
    // Limite di velocità
    private static final int MAX_SPEED = 7;

    private final CheckpointTargetFinder targetFinder = new CheckpointTargetFinder();
    private final CheckpointManager checkpointManager = new CheckpointManager();

    @Override
    public Vector getNextAcceleration(Player player, GameState gameState) {
        Position current = player.getPosition();
        Vector velocity = player.getVelocity();

        // Trova il prossimo checkpoint
        Position target = targetFinder.findNextTarget(player, gameState);
        if (target == null) {
            // Se non c'è checkpoint, fermo
            return new Vector(0,0);
        }

        // BFS: coda, visited con (posizione, velocità)
        Queue<BFSNode> queue = new LinkedList<>();
        Set<BFSNode> visited = new HashSet<>();

        // Stato iniziale
        BFSNode start = new BFSNode(current, velocity, null, null);
        queue.add(start);
        visited.add(start);

        BFSNode goal = null;

        // Espansione BFS
        while (!queue.isEmpty()) {
            BFSNode node = queue.poll();

            // Se abbiamo raggiunto il target
            if (node.position.equals(target)) {
                goal = node;
                break;
            }

            // Genera vicini
            for (Vector acc : ACCELERATIONS) {
                Vector newVel = node.velocity.add(acc);
                if (Math.abs(newVel.getDx()) > MAX_SPEED || Math.abs(newVel.getDy()) > MAX_SPEED) {
                    continue;
                }
                Position newPos = node.position.move(newVel);

                // Verifica se non sbatte contro un muro / collisioni (solo muri, per BFS)
                if (!movementManager.validateMoveTemp(node.position, newVel, gameState.getTrack())) {
                    continue;
                }

                BFSNode nextNode = new BFSNode(newPos, newVel, node, acc);
                if (!visited.contains(nextNode)) {
                    visited.add(nextNode);
                    queue.add(nextNode);
                }
            }
        }

        // Se non trovata alcuna via
        if (goal == null) {
            return new Vector(0,0);
        }

        // Ricostruiamo la catena per ottenere la prima accelerazione dal nostro stato attuale
        BFSNode cur = goal;
        // Risaliamo finché c'è un parent
        while (cur.parent != null && cur.parent != start) {
            cur = cur.parent;
        }
        // Se cur.parent == null => era lo start
        // Se cur.parent == start => cur è la prima mossa
        Vector chosenAcc = (cur.accApplied != null) ? cur.accApplied : new Vector(0,0);

        // Puoi anche verificare se la mossa è ancora valida “in tempo reale”
        Vector finalVelocity = player.getVelocity().add(chosenAcc);
        Position finalPos = player.getPosition().move(finalVelocity);
        if (movementManager.validateMove(player, chosenAcc, gameState)) {
            // Eventuali checkpoint attraversati
            checkpointManager.checkCrossedCheckpoints(player, current, finalPos, gameState.getTrack());
            return chosenAcc;
        }

        // fallback
        return new Vector(0,0);
    }

    // Nodo BFS
    private static class BFSNode {
        final Position position;
        final Vector velocity;
        final BFSNode parent;
        final Vector accApplied; // l'accelerazione applicata per passare dal parent a questo

        BFSNode(Position position, Vector velocity, BFSNode parent, Vector accApplied) {
            this.position = position;
            this.velocity = velocity;
            this.parent = parent;
            this.accApplied = accApplied;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BFSNode)) return false;
            BFSNode other = (BFSNode) o;
            return this.position.equals(other.position) && this.velocity.equals(other.velocity);
        }

        @Override
        public int hashCode() {
            return position.hashCode()*31 + velocity.hashCode();
        }
    }
}
