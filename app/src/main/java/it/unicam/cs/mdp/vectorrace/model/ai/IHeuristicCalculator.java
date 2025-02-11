package it.unicam.cs.mdp.vectorrace.model.ai;

import it.unicam.cs.mdp.vectorrace.model.core.Position;

/**
 * Interfaccia che definisce il calcolo della funzione euristica
 * per l'algoritmo A*.
 */
public interface IHeuristicCalculator {
    /**
     * Calcola il valore euristico tra due posizioni.
     * 
     * @param current la posizione corrente
     * @param target la posizione target
     * @return il valore euristico stimato
     */
    double calculate(Position current, Position target);
}