package it.unicam.cs.mdp.vectorrace.model.core;

/**
 * Enum che rappresenta i diversi tipi di celle del circuito.
 */
public enum CellType {
    WALL, // Muro, cella non percorribile
    ROAD, // Strada, cella percorribile
    START, // Linea di partenza
    FINISH, // Linea di arrivo
    CHECKPOINT; // Checkpoint (può essere rappresentato da '@' oppure da una cifra)

    /**
     * Restituisce il CellType corrispondente al carattere. Se il carattere è
     * una cifra, restituisce CHECKPOINT.
     *
     * @param c carattere letto dal file
     * @return CellType associato
     */
    public static CellType fromChar(char c) {
        if (Character.isDigit(c)) {
            return CHECKPOINT;
        }
        switch (c) {
            case '#':
                return WALL;
            case '.':
                return ROAD;
            case 'S':
                return START;
            case '*':
                return FINISH;
            default:
                return ROAD; // Per default, se non è un carattere riconosciuto, considera ROAD
        }
    }
}