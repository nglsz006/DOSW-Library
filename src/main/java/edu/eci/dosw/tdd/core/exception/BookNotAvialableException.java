package edu.eci.dosw.tdd.core.exception;

/**
 * Excepción lanzada cuando un libro no está disponible para préstamo.
 */
public class BookNotAvialableException extends Exception {
    public BookNotAvialableException(String message) {
        super(message);
    }
}
