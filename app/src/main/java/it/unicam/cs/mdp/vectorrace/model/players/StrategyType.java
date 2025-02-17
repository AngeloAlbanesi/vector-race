package it.unicam.cs.mdp.vectorrace.model.players;

/**
 * Enum che definisce i tipi di strategia disponibili per i bot.
 */
public enum StrategyType {
    BFS(1),
    ASTAR(2);

    private final int code;

    StrategyType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StrategyType fromCode(int code) {
        for (StrategyType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return BFS; // Default strategy
    }
}