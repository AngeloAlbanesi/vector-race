package it.unicam.cs.mdp.vectorrace.view.output;

/**
 * Gestore dell'output su console.
 * Fornisce metodi per la visualizzazione di messaggi sulla console.
 */
public class ConsoleOutputHandler implements IOutputHandler {
    
    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public void printError(String message) {
        System.err.println("ERROR: " + message);
    }

    /**
     * Pulisce lo schermo della console (supporto limitato).
     */
    public void clear() {
        // Tentativo di pulire la console in sistemi Unix-like
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        // In caso di Windows o se il metodo precedente non funziona
        // stampa alcune righe vuote
        for(int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}