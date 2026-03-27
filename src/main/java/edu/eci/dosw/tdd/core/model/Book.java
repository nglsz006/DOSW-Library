package edu.eci.dosw.tdd.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private String title;
    private String author;
    private Long id;
    private int totalCopies;
    private int availableCopies;

    public Book(String title, String author, Long id) {
        this.title = title;
        this.author = author;
        this.id = id;
    }
}
