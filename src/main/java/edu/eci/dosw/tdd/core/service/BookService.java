package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.validators.BookValidator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar libros de la biblioteca.
 * Usa un Map<Book, Integer> para almacenar el libro y la cantidad de ejemplares.
 * Operaciones básicas implementadas con Streams.
 * Patrón Singleton gestionado por Spring (@Service).
 */
@Service
public class BookService {

    private final Map<Book, Integer> bookCatalog = new HashMap<>();

    /**
     * Agrega un libro al catálogo. Si ya existe, incrementa la cantidad de ejemplares.
     */
    public Book addBook(String title, String author) {
        Book book = new Book(title, author, IdGeneratorUtil.getInstance().generateBookId());
        BookValidator.validate(book);

        // Buscar si ya existe un libro con el mismo título y autor usando stream
        Optional<Book> existing = bookCatalog.keySet().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title) && b.getAuthor().equalsIgnoreCase(author))
                .findFirst();

        if (existing.isPresent()) {
            bookCatalog.merge(existing.get(), 1, Integer::sum);
            return existing.get();
        }

        bookCatalog.put(book, 1);
        return book;
    }

    /**
     * Obtener todos los libros del catálogo usando stream.
     */
    public List<Book> getAllBooks() {
        return bookCatalog.keySet().stream()
                .collect(Collectors.toList());
    }

    /**
     * Obtener un libro por su ID usando stream.
     */
    public Optional<Book> getBookById(int id) {
        return bookCatalog.keySet().stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }

    /**
     * Actualizar la disponibilidad de un libro.
     */
    public void updateBookAvailability(int bookId, boolean available) throws BookNotAvialableException {
        Book book = getBookById(bookId)
                .orElseThrow(() -> new BookNotAvialableException("No se encontró el libro con ID: " + bookId));
        book.setAvailable(available);
    }

    /**
     * Obtener la cantidad de ejemplares de un libro.
     */
    public int getBookCopies(int bookId) {
        return bookCatalog.entrySet().stream()
                .filter(entry -> entry.getKey().getId() == bookId)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(0);
    }

    /**
     * Obtener libros disponibles usando stream.
     */
    public List<Book> getAvailableBooks() {
        return bookCatalog.keySet().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Limpiar catálogo (útil para tests).
     */
    public void clear() {
        bookCatalog.clear();
    }

    /**
     * Obtener el mapa completo de libros y ejemplares.
     */
    public Map<Book, Integer> getBookCatalog() {
        return Collections.unmodifiableMap(bookCatalog);
    }
}
