package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.model.Book;

public class BookMapper {

    private BookMapper() {
    }

    public static BookDTO toDTO(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getTotalCopies(), book.getAvailableCopies());
    }

    public static Book toModel(BookDTO dto) {
        Book book = new Book(dto.getTitle(), dto.getAuthor(), dto.getId());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getAvailableCopies());
        return book;
    }
}
