package edu.eci.dosw.tdd.core.exception;

/**
 * Excepción lanzada cuando un usuario excede el límite de préstamos activos.
 */
public class LoanLimitExceededException extends Exception {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
