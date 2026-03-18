package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
        IdGeneratorUtil.getInstance().reset();
    }

    // ===================== ESCENARIOS EXITOSOS =====================

    @Test
    void shouldAddBookSuccessfully() {
        Book book = bookService.addBook("Cien Años de Soledad", "Gabriel García Márquez");

        assertNotNull(book);
        assertEquals("Cien Años de Soledad", book.getTitle());
        assertEquals("Gabriel García Márquez", book.getAuthor());
        assertTrue(book.isAvailable());
    }

    @Test
    void shouldIncrementCopiesWhenAddingDuplicateBook() {
        bookService.addBook("El Quijote", "Cervantes");
        bookService.addBook("El Quijote", "Cervantes");

        assertEquals(1, bookService.getAllBooks().size());
        int bookId = bookService.getAllBooks().get(0).getId();
        assertEquals(2, bookService.getBookCopies(bookId));
    }

    @Test
    void shouldGetAllBooks() {
        bookService.addBook("Libro A", "Autor A");
        bookService.addBook("Libro B", "Autor B");

        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
    }

    @Test
    void shouldGetBookById() {
        Book added = bookService.addBook("Libro Test", "Autor Test");

        Optional<Book> found = bookService.getBookById(added.getId());
        assertTrue(found.isPresent());
        assertEquals("Libro Test", found.get().getTitle());
    }

    @Test
    void shouldUpdateBookAvailability() throws BookNotAvialableException {
        Book book = bookService.addBook("Libro X", "Autor X");
        assertTrue(book.isAvailable());

        bookService.updateBookAvailability(book.getId(), false);
        assertFalse(book.isAvailable());
    }

    @Test
    void shouldGetAvailableBooks() {
        Book book1 = bookService.addBook("Disponible", "Autor");
        Book book2 = bookService.addBook("No Disponible", "Autor");
        book2.setAvailable(false);

        List<Book> available = bookService.getAvailableBooks();
        assertEquals(1, available.size());
        assertEquals("Disponible", available.get(0).getTitle());
    }

    @Test
    void shouldReturnEmptyListWhenNoBooks() {
        List<Book> books = bookService.getAllBooks();
        assertTrue(books.isEmpty());
    }

    // ===================== ESCENARIOS DE ERROR =====================

    @Test
    void shouldReturnEmptyOptionalForNonExistentBookId() {
        Optional<Book> result = bookService.getBookById(999);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentBook() {
        assertThrows(BookNotAvialableException.class,
                () -> bookService.updateBookAvailability(999, true));
    }

    @Test
    void shouldThrowExceptionWhenAddingBookWithNullTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(null, "Autor"));
    }

    @Test
    void shouldThrowExceptionWhenAddingBookWithEmptyTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("", "Autor"));
    }

    @Test
    void shouldThrowExceptionWhenAddingBookWithNullAuthor() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("Título", null));
    }

    @Test
    void shouldReturnZeroCopiesForNonExistentBook() {
        assertEquals(0, bookService.getBookCopies(999));
    }
}
