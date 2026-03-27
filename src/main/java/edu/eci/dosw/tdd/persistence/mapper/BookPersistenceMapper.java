package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;

public class BookPersistenceMapper {

    private BookPersistenceMapper() {}

    public static Book toDomain(BookEntity entity) {
        Book book = new Book(entity.getTitle(), entity.getAuthor(), entity.getId());
        book.setTotalCopies(entity.getTotalCopies());
        book.setAvailableCopies(entity.getAvailableCopies());
        return book;
    }

    public static BookEntity toEntity(Book book) {
        return BookEntity.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }
}
