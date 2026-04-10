package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;

public class BookDocumentMapper {

    private BookDocumentMapper() {}

    public static Book toDomain(BookDocument doc) {
        return Book.builder()
                .id(null)
                .title(doc.getTitle())
                .author(doc.getAuthor())
                .isbn(doc.getIsbn())
                .categories(doc.getCategories())
                .publicationType(doc.getPublicationType())
                .publicationDate(doc.getPublicationDate() != null
                        ? java.time.LocalDate.parse(doc.getPublicationDate()) : null)
                .addedDate(doc.getAddedDate() != null
                        ? java.time.LocalDate.parse(doc.getAddedDate()) : null)
                .availabilityStatus(doc.getAvailabilityStatus())
                .totalCopies(doc.getTotalCopies())
                .availableCopies(doc.getAvailableCopies())
                .loanedCopies(doc.getLoanedCopies())
                .pages(doc.getPages())
                .language(doc.getLanguage())
                .publisher(doc.getPublisher())
                .build();
    }

    public static BookDocument toDocument(Book book) {
        return BookDocument.builder()
                .id(book.getId() != null ? book.getId().toString() : null)
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .categories(book.getCategories())
                .publicationType(book.getPublicationType())
                .publicationDate(book.getPublicationDate() != null
                        ? book.getPublicationDate().toString() : null)
                .addedDate(book.getAddedDate() != null
                        ? book.getAddedDate().toString() : null)
                .availabilityStatus(book.getAvailabilityStatus())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .loanedCopies(book.getLoanedCopies())
                .pages(book.getPages())
                .language(book.getLanguage())
                .publisher(book.getPublisher())
                .build();
    }
}
