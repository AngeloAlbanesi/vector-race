package it.unicam.cs.mdp.vectorrace.view;

/**
 * Interfaccia che definisce i metodi specifici per la CLI.
 */
public interface CLISpecific {
    /**
     * Mostra il menu di selezione del circuito.
     */
    void showCircuitSelection();

    /**
     * Mostra il menu principale del gioco.
     */
    void showGameMenu();

    /**
     * Imposta lo stato di esecuzione del gioco.
     * 
     * @param running lo stato di esecuzione da impostare
     */
    void setGameRunning(boolean running);

    /**
     * Restituisce lo stato di esecuzione del gioco.
     * 
     * @return true se il gioco è in esecuzione, false altrimenti
     */
    boolean isGameRunning();
}