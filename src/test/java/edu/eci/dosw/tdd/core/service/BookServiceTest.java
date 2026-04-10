package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private JpaBookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    // ===================== ESCENARIOS EXITOSOS =====================

    @Test
    void shouldAddBookSuccessfully() throws BookNotAvialableException {
        Book book = bookService.addBook("Cien Años de Soledad", "Gabriel García Márquez", 3);

        assertNotNull(book);
        assertNotNull(book.getId());
        assertEquals("Cien Años de Soledad", book.getTitle());
        assertEquals("Gabriel García Márquez", book.getAuthor());
        assertEquals(3, book.getTotalCopies());
        assertEquals(3, book.getAvailableCopies());
    }

    @Test
    void shouldGetAllBooks() throws BookNotAvialableException {
        bookService.addBook("Libro A", "Autor A", 1);
        bookService.addBook("Libro B", "Autor B", 2);

        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
    }

    @Test
    void shouldGetBookById() throws BookNotAvialableException {
        Book added = bookService.addBook("Libro Test", "Autor Test", 1);

        Optional<Book> found = bookService.getBookById(added.getId());
        assertTrue(found.isPresent());
        assertEquals("Libro Test", found.get().getTitle());
    }

    @Test
    void shouldGetAvailableBooks() throws BookNotAvialableException {
        bookService.addBook("Disponible", "Autor", 2);
        bookService.addBook("Sin Stock", "Autor", 0);

        List<Book> available = bookService.getAvailableBooks();
        assertEquals(1, available.size());
        assertEquals("Disponible", available.get(0).getTitle());
    }

    @Test
    void shouldUpdateBookStock() throws BookNotAvialableException {
        Book book = bookService.addBook("Libro X", "Autor X", 2);

        Book updated = bookService.updateBookStock(book.getId(), 5);

        assertEquals(5, updated.getTotalCopies());
        assertEquals(5, updated.getAvailableCopies());
    }

    @Test
    void shouldReturnEmptyListWhenNoBooks() {
        List<Book> books = bookService.getAllBooks();
        assertTrue(books.isEmpty());
    }

    @Test
    void shouldPersistBookAndRetrieveById() throws BookNotAvialableException {
        Book saved = bookService.addBook("Persistencia", "Autor", 1);
        Long id = saved.getId();

        Optional<Book> found = bookService.getBookById(id);
        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
    }

    // ===================== ESCENARIOS DE ERROR =====================

    @Test
    void shouldReturnEmptyOptionalForNonExistentBookId() {
        Optional<Book> result = bookService.getBookById(9999L);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentBook() {
        assertThrows(BookNotAvialableException.class,
                () -> bookService.updateBookStock(9999L, 5));
    }

    @Test
    void shouldThrowExceptionWhenAddingBookWithNullTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(null, "Autor", 1));
    }

    @Test
    void shouldThrowExceptionWhenAddingBookWithEmptyTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("", "Autor", 1));
    }

    @Test
    void shouldThrowExceptionWhenAddingBookWithNullAuthor() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("Título", null, 1));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingStockToZero() throws BookNotAvialableException {
        Book book = bookService.addBook("Libro Z", "Autor Z", 2);
        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBookStock(book.getId(), 0));
    }
}
