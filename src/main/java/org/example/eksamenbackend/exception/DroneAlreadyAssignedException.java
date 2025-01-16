package org.example.eksamenbackend.exception;


public class DroneAlreadyAssignedException extends RuntimeException {
    public DroneAlreadyAssignedException(String message) {
        super(message);
    }
}
