package it.unicam.cs.mdp.vectorrace.view.output;

/**
 * Interfaccia per la gestione dell'output.
 * Astrae le operazioni di output per rispettare il principio di inversione delle dipendenze.
 */
public interface IOutputHandler {
    /**
     * Visualizza un messaggio.
     * @param message il messaggio da visualizzare
     */
    void display(String message);

    /**
     * Visualizza un messaggio con una nuova riga alla fine.
     * @param message il messaggio da visualizzare
     */
    void displayLine(String message);

    /**
     * Pulisce lo schermo.
     */
    void clearScreen();

    /**
     * Forza il flush dell'output.
     */
    void flush();
}