package edu.eci.dosw.tdd.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String title;
    private String author;
    private Long id;
    private int totalCopies;
    private int availableCopies;

    private String isbn;
    private List<String> categories;
    private String publicationType;
    private LocalDate publicationDate;
    private LocalDate addedDate;
    private int loanedCopies;
    private String availabilityStatus;
    private int pages;
    private String language;
    private String publisher;

    public Book(String title, String author, Long id) {
        this.title = title;
        this.author = author;
        this.id = id;
    }
}
