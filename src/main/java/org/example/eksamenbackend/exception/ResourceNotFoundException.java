package org.example.eksamenbackend.exception;

/**
 * Kastes, når en bestemt ressource ikke kan findes i databasen.
 * Eksempelvis en pizza, en levering eller en drone, der ikke eksisterer.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
