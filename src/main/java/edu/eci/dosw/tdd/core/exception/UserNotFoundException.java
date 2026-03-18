package edu.eci.dosw.tdd.core.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario en el sistema.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
