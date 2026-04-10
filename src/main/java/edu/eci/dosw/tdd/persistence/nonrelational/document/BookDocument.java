package edu.eci.dosw.tdd.persistence.nonrelational.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDocument {

    @Id
    private String id;

    private String title;
    private String author;
    private String isbn;
    private List<String> categories;
    private String publicationType;
    private String publicationDate;
    private String addedDate;
    private String availabilityStatus;
    private int totalCopies;
    private int availableCopies;
    private int loanedCopies;
    private int pages;
    private String language;
    private String publisher;
}
