package edu.eci.dosw.tdd.model;

import lombok.Data;
@Data
public class Loan {
    private Book book;
    private User user;
    private String loanDate;
    private boolean status;
    private String returnDate;

}
