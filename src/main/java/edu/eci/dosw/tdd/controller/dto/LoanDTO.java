package edu.eci.dosw.tdd.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;
    private String loanDate;
    private String returnDate;
    private String status;
}
