package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;

public class BookPersistenceMapper {

    private BookPersistenceMapper() {}

    public static Book toDomain(BookEntity entity) {
        return Book.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .totalCopies(entity.getTotalCopies())
                .availableCopies(entity.getAvailableCopies())
                .isbn(entity.getIsbn())
                .categories(entity.getCategories())
                .publicationType(entity.getPublicationType())
                .publicationDate(entity.getPublicationDate())
                .addedDate(entity.getAddedDate())
                .loanedCopies(entity.getLoanedCopies())
                .availabilityStatus(entity.getAvailabilityStatus())
                .pages(entity.getPages())
                .language(entity.getLanguage())
                .publisher(entity.getPublisher())
                .build();
    }

    public static BookEntity toEntity(Book book) {
        return BookEntity.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .isbn(book.getIsbn())
                .categories(book.getCategories())
                .publicationType(book.getPublicationType())
                .publicationDate(book.getPublicationDate())
                .addedDate(book.getAddedDate())
                .loanedCopies(book.getLoanedCopies())
                .availabilityStatus(book.getAvailabilityStatus())
                .pages(book.getPages())
                .language(book.getLanguage())
                .publisher(book.getPublisher())
                .build();
    }
}
