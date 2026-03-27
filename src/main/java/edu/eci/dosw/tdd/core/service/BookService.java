package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.validators.BookValidator;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book addBook(String title, String author, int totalCopies) {
        Book book = new Book(title, author, null);
        BookValidator.validate(book);

        BookEntity entity = BookEntity.builder()
                .title(title)
                .author(author)
                .totalCopies(totalCopies)
                .availableCopies(totalCopies)
                .build();

        BookEntity saved = bookRepository.save(entity);
        return BookPersistenceMapper.toDomain(saved);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookPersistenceMapper::toDomain);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0).stream()
                .map(BookPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public Book updateBookStock(Long bookId, int totalCopies) throws BookNotAvialableException {
        if (totalCopies <= 0) {
            throw new IllegalArgumentException("totalCopies debe ser mayor que 0");
        }
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotAvialableException("No se encontró el libro con ID: " + bookId));

        int borrowed = entity.getTotalCopies() - entity.getAvailableCopies();
        if (totalCopies < borrowed) {
            throw new IllegalArgumentException("totalCopies no puede ser menor que las copias actualmente prestadas: " + borrowed);
        }

        entity.setTotalCopies(totalCopies);
        entity.setAvailableCopies(totalCopies - borrowed);
        return BookPersistenceMapper.toDomain(bookRepository.save(entity));
    }

    @Transactional
    public void decrementAvailableCopies(Long bookId) throws BookNotAvialableException {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotAvialableException("No se encontró el libro con ID: " + bookId));
        if (entity.getAvailableCopies() == 0) {
            throw new BookNotAvialableException("El libro no tiene copias disponibles");
        }
        entity.setAvailableCopies(entity.getAvailableCopies() - 1);
        bookRepository.save(entity);
    }

    @Transactional
    public void incrementAvailableCopies(Long bookId) throws BookNotAvialableException {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotAvialableException("No se encontró el libro con ID: " + bookId));
        if (entity.getAvailableCopies() >= entity.getTotalCopies()) {
            throw new BookNotAvialableException("No se puede incrementar: availableCopies ya es igual a totalCopies");
        }
        entity.setAvailableCopies(entity.getAvailableCopies() + 1);
        bookRepository.save(entity);
    }
}
