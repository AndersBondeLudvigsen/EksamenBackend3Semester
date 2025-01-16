package org.example.eksamenbackend.exception;

/**
 * Kastes, når en bestemt operation ikke er tilladt.
 * F.eks. at afslutte en levering, der ingen drone har.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
