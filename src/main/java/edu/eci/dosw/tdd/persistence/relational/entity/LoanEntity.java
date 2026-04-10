package edu.eci.dosw.tdd.persistence.relational.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalDate loanDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private String status;

    @ElementCollection
    @CollectionTable(name = "loan_history", joinColumns = @JoinColumn(name = "loan_id"))
    @Builder.Default
    private List<LoanHistoryEmbeddable> history = new ArrayList<>();
}
