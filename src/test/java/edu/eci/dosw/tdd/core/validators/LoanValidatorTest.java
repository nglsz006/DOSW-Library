package edu.eci.dosw.tdd.core.validators;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanValidatorTest {

    @Test
    void shouldPassValidationForValidLoan() {
        Loan loan = new Loan(new Book("T", "A", 1L), new User(1L, "N", null, null),
                "2025-01-01", "2025-01-15", Loan.STATUS_ACTIVE);
        assertDoesNotThrow(() -> LoanValidator.validate(loan));
    }

    @Test
    void shouldThrowExceptionForNullLoan() {
        assertThrows(IllegalArgumentException.class, () -> LoanValidator.validate(null));
    }

    @Test
    void shouldThrowExceptionForLoanWithNullBook() {
        Loan loan = new Loan(null, new User(1L, "N", null, null), "2025-01-01", "2025-01-15", Loan.STATUS_ACTIVE);
        assertThrows(IllegalArgumentException.class, () -> LoanValidator.validate(loan));
    }

    @Test
    void shouldThrowExceptionForLoanWithNullUser() {
        Loan loan = new Loan(new Book("T", "A", 1L), null, "2025-01-01", "2025-01-15", Loan.STATUS_ACTIVE);
        assertThrows(IllegalArgumentException.class, () -> LoanValidator.validate(loan));
    }
}
