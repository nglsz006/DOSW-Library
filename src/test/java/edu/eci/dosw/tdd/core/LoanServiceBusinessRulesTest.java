package edu.eci.dosw.tdd.core;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaBookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaLoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceBusinessRulesTest {

    @Mock
    private JpaLoanRepository loanRepository;

    @Mock
    private JpaBookRepository bookRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private LoanService loanService;

    private UserEntity buildUser(Long id) {
        return UserEntity.builder().id(id).name("Usuario").username("user1").role("USER").build();
    }

    private BookEntity buildBook(Long id, int totalCopies, int availableCopies) {
        return BookEntity.builder()
                .id(id).title("Libro").author("Autor")
                .totalCopies(totalCopies).availableCopies(availableCopies)
                .build();
    }

    @Test
    void crearPrestamo_sinCopias_debeRechazar() {
        Long userId = 1L, bookId = 10L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(buildBook(bookId, 3, 0)));

        BookNotAvialableException ex = assertThrows(BookNotAvialableException.class,
                () -> loanService.createLoan(userId, bookId));
        assertTrue(ex.getMessage().contains("no está disponible"));
    }

    @Test
    void crearPrestamo_limiteExcedido_debeRechazar() {
        Long userId = 1L, bookId = 10L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(buildBook(bookId, 5, 2)));
        when(loanRepository.countByUserIdAndStatus(userId, Loan.STATUS_ACTIVE)).thenReturn(3L);

        LoanLimitExceededException ex = assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan(userId, bookId));
        assertTrue(ex.getMessage().contains("límite de 3 préstamos activos"));
    }

    @Test
    void crearPrestamo_exitoso_decrementaCopias() throws Exception {
        Long userId = 1L, bookId = 10L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(buildBook(bookId, 3, 3)));
        when(loanRepository.countByUserIdAndStatus(userId, Loan.STATUS_ACTIVE)).thenReturn(0L);

        assertDoesNotThrow(() -> loanService.createLoan(userId, bookId));
        verify(bookService).decrementAvailableCopies(bookId);
    }

    @Test
    void devolverLibro_prestamoYaRetornado_debeRechazar() {
        Long userId = 1L, bookId = 10L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)));
        when(loanRepository.findByUserIdAndBookIdAndStatus(userId, bookId, Loan.STATUS_ACTIVE))
                .thenReturn(Optional.empty());

        BookNotAvialableException ex = assertThrows(BookNotAvialableException.class,
                () -> loanService.returnBook(userId, bookId));
        assertTrue(ex.getMessage().contains("No se encontró un préstamo activo"));
    }

    @Test
    void devolverLibro_exitoso_incrementaCopias() throws Exception {
        Long userId = 1L, bookId = 10L;
        BookEntity book = buildBook(bookId, 3, 1);
        UserEntity user = buildUser(userId);
        LoanEntity loanEntity = LoanEntity.builder()
                .id(1L).book(book).user(user)
                .loanDate(LocalDate.now()).returnDate(LocalDate.now().plusDays(14))
                .status(Loan.STATUS_ACTIVE)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanRepository.findByUserIdAndBookIdAndStatus(userId, bookId, Loan.STATUS_ACTIVE))
                .thenReturn(Optional.of(loanEntity));

        assertDoesNotThrow(() -> loanService.returnBook(userId, bookId));

        assertEquals(Loan.STATUS_RETURNED, loanEntity.getStatus());
        verify(bookService).incrementAvailableCopies(bookId);
    }
}
