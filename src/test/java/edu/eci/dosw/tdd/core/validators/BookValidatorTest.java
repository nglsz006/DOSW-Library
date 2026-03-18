package edu.eci.dosw.tdd.core.validators;

import edu.eci.dosw.tdd.core.model.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookValidatorTest {

    @Test
    void shouldPassValidationForValidBook() {
        Book book = new Book("Título", "Autor", 1);
        assertDoesNotThrow(() -> BookValidator.validate(book));
    }

    @Test
    void shouldThrowExceptionForNullBook() {
        assertThrows(IllegalArgumentException.class, () -> BookValidator.validate(null));
    }

    @Test
    void shouldThrowExceptionForEmptyTitle() {
        Book book = new Book("", "Autor", 1);
        assertThrows(IllegalArgumentException.class, () -> BookValidator.validate(book));
    }

    @Test
    void shouldThrowExceptionForNullAuthor() {
        Book book = new Book("Título", null, 1);
        assertThrows(IllegalArgumentException.class, () -> BookValidator.validate(book));
    }
}
