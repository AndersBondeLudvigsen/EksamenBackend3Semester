package org.example.eksamenbackend.exception;

/**
 * Kastes, når en drone allerede er tildelt en levering eller er optaget.
 * F.eks. ved forsøg på at tildele en drone til flere leveringer samtidigt.
 */
public class DroneAlreadyAssignedException extends RuntimeException {
    public DroneAlreadyAssignedException(String message) {
        super(message);
    }
}
