package edu.eci.dosw.tdd.persistence.relational.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanHistoryEmbeddable {
    private String status;
    private LocalDate changedAt;
}
