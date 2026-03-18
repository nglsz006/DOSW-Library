package edu.eci.dosw.tdd.core.validators;

import edu.eci.dosw.tdd.core.model.Loan;

/**
 * Patrón Strategy: encapsula la lógica de validación de préstamos.
 */
public class LoanValidator {

    public static void validate(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("El préstamo no puede ser nulo");
        }
        if (loan.getBook() == null) {
            throw new IllegalArgumentException("El préstamo debe tener un libro asociado");
        }
        if (loan.getUser() == null) {
            throw new IllegalArgumentException("El préstamo debe tener un usuario asociado");
        }
    }
}
