package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) throws BookNotAvialableException {
        Book book = bookService.addBook(bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getTotalCopies());
        return new ResponseEntity<>(BookMapper.toDTO(book), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks().stream()
                .map(BookMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) throws BookNotAvialableException {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new BookNotAvialableException("No se encontró el libro con ID: " + id));
        return ResponseEntity.ok(BookMapper.toDTO(book));
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<BookDTO> updateBookStock(@PathVariable Long id, @RequestParam int totalCopies)
            throws BookNotAvialableException {
        Book book = bookService.updateBookStock(id, totalCopies);
        return ResponseEntity.ok(BookMapper.toDTO(book));
    }
}
