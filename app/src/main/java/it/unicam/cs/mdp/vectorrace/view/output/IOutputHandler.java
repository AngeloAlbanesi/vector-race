package it.unicam.cs.mdp.vectorrace.view.output;

/**
 * Interfaccia per la gestione dell'output del gioco.
 * Definisce i metodi base per la visualizzazione di messaggi.
 */
public interface IOutputHandler {
    
    /**
     * Visualizza una stringa senza andare a capo.
     *
     * @param message Il messaggio da visualizzare
     */
    void print(String message);

    /**
     * Visualizza una stringa andando a capo.
     *
     * @param message Il messaggio da visualizzare
     */
    void println(String message);

    /**
     * Visualizza un messaggio di errore.
     *
     * @param message Il messaggio di errore da visualizzare
     */
    void printError(String message);

    /**
     * Pulisce lo schermo.
     */
    void clear();
    
    /**
     * Forza il flush del buffer di output.
     */
    default void flush() {
        System.out.flush();
    }
}