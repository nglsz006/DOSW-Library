package edu.eci.dosw.tdd.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {
    private int bookId;
    private String bookTitle;
    private int userId;
    private String userName;
    private String loanDate;
    private String returnDate;
    private String status;
}
