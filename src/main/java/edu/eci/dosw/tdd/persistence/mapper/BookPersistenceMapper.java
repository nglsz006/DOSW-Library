package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;

public class BookPersistenceMapper {

    private BookPersistenceMapper() {}

    public static Book toDomain(BookEntity entity) {
        return new Book(
                entity.getTitle(),
                entity.getAuthor(),
                entity.getId().intValue(),
                entity.getAvailableCopies() > 0
        );
    }

    public static BookEntity toEntity(Book book) {
        return BookEntity.builder()
                .id((long) book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build();
    }
}
