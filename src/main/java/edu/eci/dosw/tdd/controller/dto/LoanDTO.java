package edu.eci.dosw.tdd.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {
    @NotNull @Min(1)
    private Long bookId;
    private String bookTitle;
    @NotNull @Min(1)
    private Long userId;
    private String userName;
    private String loanDate;
    private String returnDate;
    private String status;
}
