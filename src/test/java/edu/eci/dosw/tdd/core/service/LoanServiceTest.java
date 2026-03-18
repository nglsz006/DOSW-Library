package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        IdGeneratorUtil.getInstance().reset();
        bookService = new BookService();
        userService = new UserService();
        loanService = new LoanService(bookService, userService);
    }

    // ===================== ESCENARIOS EXITOSOS =====================

    @Test
    void shouldCreateLoanSuccessfully()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Ana García");
        Book book = bookService.addBook("Java Efectivo", "Joshua Bloch");

        Loan loan = loanService.createLoan(user.getId(), book.getId());

        assertNotNull(loan);
        assertEquals(Loan.STATUS_ACTIVE, loan.getStatus());
        assertEquals("Java Efectivo", loan.getBook().getTitle());
        assertEquals("Ana García", loan.getUser().getName());
        assertFalse(book.isAvailable());
    }

    @Test
    void shouldReturnBookSuccessfully()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Pedro");
        Book book = bookService.addBook("Clean Code", "Robert Martin");

        loanService.createLoan(user.getId(), book.getId());
        assertFalse(book.isAvailable());

        Loan returned = loanService.returnBook(user.getId(), book.getId());

        assertEquals(Loan.STATUS_RETURNED, returned.getStatus());
        assertTrue(book.isAvailable());
    }

    @Test
    void shouldGetAllLoans()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Luisa");
        Book book1 = bookService.addBook("Libro 1", "Autor 1");
        Book book2 = bookService.addBook("Libro 2", "Autor 2");

        loanService.createLoan(user.getId(), book1.getId());
        loanService.createLoan(user.getId(), book2.getId());

        assertEquals(2, loanService.getAllLoans().size());
    }

    @Test
    void shouldGetActiveLoans()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Mario");
        Book book1 = bookService.addBook("Libro A", "Autor A");
        Book book2 = bookService.addBook("Libro B", "Autor B");

        loanService.createLoan(user.getId(), book1.getId());
        loanService.createLoan(user.getId(), book2.getId());
        loanService.returnBook(user.getId(), book1.getId());

        List<Loan> active = loanService.getActiveLoans();
        assertEquals(1, active.size());
        assertEquals("Libro B", active.get(0).getBook().getTitle());
    }

    @Test
    void shouldGetLoansByUserId()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user1 = userService.registerUser("Usuario 1");
        User user2 = userService.registerUser("Usuario 2");
        Book book1 = bookService.addBook("Libro 1", "Autor");
        Book book2 = bookService.addBook("Libro 2", "Autor");

        loanService.createLoan(user1.getId(), book1.getId());
        loanService.createLoan(user2.getId(), book2.getId());

        List<Loan> user1Loans = loanService.getLoansByUserId(user1.getId());
        assertEquals(1, user1Loans.size());
    }

    @Test
    void shouldAllowNewLoanAfterReturningBook()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Test User");
        Book book1 = bookService.addBook("L1", "A1");
        Book book2 = bookService.addBook("L2", "A2");
        Book book3 = bookService.addBook("L3", "A3");
        Book book4 = bookService.addBook("L4", "A4");

        loanService.createLoan(user.getId(), book1.getId());
        loanService.createLoan(user.getId(), book2.getId());
        loanService.createLoan(user.getId(), book3.getId());

        // Devolver uno y prestar otro
        loanService.returnBook(user.getId(), book1.getId());
        Loan newLoan = loanService.createLoan(user.getId(), book4.getId());

        assertNotNull(newLoan);
        assertEquals(Loan.STATUS_ACTIVE, newLoan.getStatus());
    }

    // ===================== ESCENARIOS DE ERROR =====================

    @Test
    void shouldThrowExceptionWhenUserNotFoundForLoan() {
        Book book = bookService.addBook("Libro", "Autor");

        assertThrows(UserNotFoundException.class,
                () -> loanService.createLoan(999, book.getId()));
    }

    @Test
    void shouldThrowExceptionWhenBookNotFoundForLoan() {
        User user = userService.registerUser("Test");

        assertThrows(BookNotAvialableException.class,
                () -> loanService.createLoan(user.getId(), 999));
    }

    @Test
    void shouldThrowExceptionWhenBookNotAvailable()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user1 = userService.registerUser("User 1");
        User user2 = userService.registerUser("User 2");
        Book book = bookService.addBook("Libro Único", "Autor");

        loanService.createLoan(user1.getId(), book.getId());

        assertThrows(BookNotAvialableException.class,
                () -> loanService.createLoan(user2.getId(), book.getId()));
    }

    @Test
    void shouldThrowExceptionWhenLoanLimitExceeded()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Lector Ávido");
        Book book1 = bookService.addBook("L1", "A");
        Book book2 = bookService.addBook("L2", "A");
        Book book3 = bookService.addBook("L3", "A");
        Book book4 = bookService.addBook("L4", "A");

        loanService.createLoan(user.getId(), book1.getId());
        loanService.createLoan(user.getId(), book2.getId());
        loanService.createLoan(user.getId(), book3.getId());

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan(user.getId(), book4.getId()));
    }

    @Test
    void shouldThrowExceptionWhenReturningNonExistentLoan() {
        User user = userService.registerUser("Test");
        bookService.addBook("Libro", "Autor");

        assertThrows(BookNotAvialableException.class,
                () -> loanService.returnBook(user.getId(), 999));
    }

    @Test
    void shouldThrowExceptionWhenReturningWithNonExistentUser() {
        assertThrows(UserNotFoundException.class,
                () -> loanService.returnBook(999, 1));
    }
}
