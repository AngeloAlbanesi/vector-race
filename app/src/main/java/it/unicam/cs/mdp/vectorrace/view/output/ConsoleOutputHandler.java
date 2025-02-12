package it.unicam.cs.mdp.vectorrace.view.output;

/**
 * Implementazione dell'IOutputHandler per l'output su console.
 * Gestisce l'output del gioco verso il terminale.
 */
public class ConsoleOutputHandler implements IOutputHandler {
    
    @Override
    public void display(String message) {
        System.out.print(message);
        flush();
    }

    @Override
    public void displayLine(String message) {
        System.out.println(message);
        flush();
    }

    @Override
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        flush();
    }

    @Override
    public void flush() {
        System.out.flush();
    }
}