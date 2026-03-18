package edu.eci.dosw.tdd.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de préstamo.
 * status: ACTIVE cuando el libro está prestado, RETURNED cuando se devuelve.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    private Book book;
    private User user;
    private String loanDate;
    private String returnDate;
    private String status;

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_RETURNED = "RETURNED";
}
