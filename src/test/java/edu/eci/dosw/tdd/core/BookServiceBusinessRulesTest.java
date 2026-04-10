package edu.eci.dosw.tdd.core;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaBookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceBusinessRulesTest {

    @Mock
    private JpaBookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void stockNegativo_debeRechazar() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBookStock(1L, 0));
        assertEquals("totalCopies debe ser mayor que 0", ex.getMessage());
    }

    @Test
    void stockMenorQuePrestadasActualmente_debeRechazar() {
        BookEntity entity = BookEntity.builder()
                .id(1L).title("Libro").author("Autor")
                .totalCopies(5).availableCopies(2)
                .build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBookStock(1L, 2));
        assertTrue(ex.getMessage().contains("no puede ser menor que las copias actualmente prestadas"));
    }

    @Test
    void stockValido_debeActualizar() throws BookNotAvialableException {
        BookEntity entity = BookEntity.builder()
                .id(1L).title("Libro").author("Autor")
                .totalCopies(3).availableCopies(3)
                .build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book result = bookService.updateBookStock(1L, 5);

        assertEquals(5, result.getTotalCopies());
    }

    @Test
    void decrementar_sinCopias_debeRechazar() {
        BookEntity entity = BookEntity.builder()
                .id(1L).title("Libro").author("Autor")
                .totalCopies(3).availableCopies(0)
                .build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));

        BookNotAvialableException ex = assertThrows(BookNotAvialableException.class,
                () -> bookService.decrementAvailableCopies(1L));
        assertEquals("El libro no tiene copias disponibles", ex.getMessage());
    }

    @Test
    void incrementar_yaAlMaximo_debeRechazar() {
        BookEntity entity = BookEntity.builder()
                .id(1L).title("Libro").author("Autor")
                .totalCopies(3).availableCopies(3)
                .build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));

        BookNotAvialableException ex = assertThrows(BookNotAvialableException.class,
                () -> bookService.incrementAvailableCopies(1L));
        assertEquals("No se puede incrementar: availableCopies ya es igual a totalCopies", ex.getMessage());
    }
}
