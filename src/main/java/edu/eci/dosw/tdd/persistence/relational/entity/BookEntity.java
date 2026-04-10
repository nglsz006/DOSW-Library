package edu.eci.dosw.tdd.persistence.relational.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int totalCopies;

    @Column(nullable = false)
    private int availableCopies;

    @Column(nullable = true)
    private String isbn;

    @ElementCollection
    @CollectionTable(name = "book_categories", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "category")
    private List<String> categories;

    @Column(nullable = true)
    private String publicationType;

    @Column(nullable = true)
    private LocalDate publicationDate;

    @Column(nullable = true)
    private LocalDate addedDate;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int loanedCopies;

    @Column(nullable = true)
    private String availabilityStatus;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int pages;

    @Column(nullable = true)
    private String language;

    @Column(nullable = true)
    private String publisher;
}
