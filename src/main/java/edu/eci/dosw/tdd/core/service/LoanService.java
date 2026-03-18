package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.validators.LoanValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar préstamos de la biblioteca.
 * Almacena una lista de préstamos.
 * Verifica disponibilidad del libro, existencia del usuario y límite de préstamos.
 * Operaciones básicas implementadas con Streams.
 * Patrón Singleton gestionado por Spring (@Service).
 */
@Service
public class LoanService {

    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;
    private static final int LOAN_DAYS = 14;

    private final List<Loan> loans = new ArrayList<>();
    private final BookService bookService;
    private final UserService userService;

    public LoanService(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }


    public Loan createLoan(int userId, int bookId)
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {

        User user = userService.findUserById(userId);

        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new BookNotAvialableException("No se encontró el libro con ID: " + bookId));

        if (!book.isAvailable()) {
            throw new BookNotAvialableException("El libro '" + book.getTitle() + "' no está disponible");
        }

        long activeLoans = loans.stream()
                .filter(loan -> loan.getUser().getId() == userId)
                .filter(loan -> Loan.STATUS_ACTIVE.equals(loan.getStatus()))
                .count();

        if (activeLoans >= MAX_ACTIVE_LOANS_PER_USER) {
            throw new LoanLimitExceededException(
                    "El usuario '" + user.getName() + "' ha alcanzado el límite de " + MAX_ACTIVE_LOANS_PER_USER + " préstamos activos");
        }

        Loan loan = new Loan(book, user, DateUtil.getCurrentDate(),
                DateUtil.calculateReturnDate(LOAN_DAYS), Loan.STATUS_ACTIVE);
        LoanValidator.validate(loan);

        book.setAvailable(false);

        loans.add(loan);
        return loan;
    }

    public Loan returnBook(int userId, int bookId)
            throws BookNotAvialableException, UserNotFoundException {

        userService.findUserById(userId);

        Loan activeLoan = loans.stream()
                .filter(loan -> loan.getUser().getId() == userId)
                .filter(loan -> loan.getBook().getId() == bookId)
                .filter(loan -> Loan.STATUS_ACTIVE.equals(loan.getStatus()))
                .findFirst()
                .orElseThrow(() -> new BookNotAvialableException(
                        "No se encontró un préstamo activo para el usuario " + userId + " y libro " + bookId));

        activeLoan.setStatus(Loan.STATUS_RETURNED);
        activeLoan.setReturnDate(DateUtil.getCurrentDate());
        activeLoan.getBook().setAvailable(true);

        return activeLoan;
    }

    public List<Loan> getAllLoans() {
        return loans.stream()
                .collect(Collectors.toList());
    }

    public List<Loan> getActiveLoans() {
        return loans.stream()
                .filter(loan -> Loan.STATUS_ACTIVE.equals(loan.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Loan> getLoansByUserId(int userId) {
        return loans.stream()
                .filter(loan -> loan.getUser().getId() == userId)
                .collect(Collectors.toList());
    }

    public void clear() {
        loans.clear();
    }
}
