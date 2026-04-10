package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaBookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaLoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private JpaLoanRepository loanRepository;

    @Autowired
    private JpaBookRepository bookRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ===================== ESCENARIOS EXITOSOS =====================

    @Test
    void shouldCreateLoanSuccessfully()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Ana García", "ana", "pass", "USER");
        Book book = bookService.addBook("Java Efectivo", "Joshua Bloch", 1);

        Loan loan = loanService.createLoan(user.getId(), book.getId());

        assertNotNull(loan);
        assertEquals(Loan.STATUS_ACTIVE, loan.getStatus());
        assertEquals("Java Efectivo", loan.getBook().getTitle());
        assertEquals("Ana García", loan.getUser().getName());
        assertNotNull(loan.getLoanDate());
        assertNotNull(loan.getReturnDate());
    }

    @Test
    void shouldDecrementAvailableCopiesOnLoan()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Test", "testuser", "pass", "USER");
        Book book = bookService.addBook("Libro", "Autor", 3);

        loanService.createLoan(user.getId(), book.getId());

        Book updated = bookService.getBookById(book.getId()).orElseThrow();
        assertEquals(2, updated.getAvailableCopies());
    }

    @Test
    void shouldReturnBookSuccessfully()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Pedro", "pedro", "pass", "USER");
        Book book = bookService.addBook("Clean Code", "Robert Martin", 1);

        loanService.createLoan(user.getId(), book.getId());
        Loan returned = loanService.returnBook(user.getId(), book.getId());

        assertEquals(Loan.STATUS_RETURNED, returned.getStatus());
        Book refreshed = bookService.getBookById(book.getId()).orElseThrow();
        assertEquals(1, refreshed.getAvailableCopies());
    }

    @Test
    void shouldGetAllLoans()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Luisa", "luisa", "pass", "USER");
        Book book1 = bookService.addBook("Libro 1", "Autor 1", 1);
        Book book2 = bookService.addBook("Libro 2", "Autor 2", 1);

        loanService.createLoan(user.getId(), book1.getId());
        loanService.createLoan(user.getId(), book2.getId());

        assertEquals(2, loanService.getAllLoans().size());
    }

    @Test
    void shouldGetActiveLoans()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Mario", "mario", "pass", "USER");
        Book book1 = bookService.addBook("Libro A", "Autor A", 1);
        Book book2 = bookService.addBook("Libro B", "Autor B", 1);

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
        User user1 = userService.registerUser("Usuario 1", "user1", "pass", "USER");
        User user2 = userService.registerUser("Usuario 2", "user2", "pass", "USER");
        Book book1 = bookService.addBook("Libro 1", "Autor", 1);
        Book book2 = bookService.addBook("Libro 2", "Autor", 1);

        loanService.createLoan(user1.getId(), book1.getId());
        loanService.createLoan(user2.getId(), book2.getId());

        List<Loan> user1Loans = loanService.getLoansByUserId(user1.getId());
        assertEquals(1, user1Loans.size());
        assertEquals("Libro 1", user1Loans.get(0).getBook().getTitle());
    }

    @Test
    void shouldAllowNewLoanAfterReturningBook()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Test User", "testuser2", "pass", "USER");
        Book book1 = bookService.addBook("L1", "A1", 1);
        Book book2 = bookService.addBook("L2", "A2", 1);
        Book book3 = bookService.addBook("L3", "A3", 1);
        Book book4 = bookService.addBook("L4", "A4", 1);

        loanService.createLoan(user.getId(), book1.getId());
        loanService.createLoan(user.getId(), book2.getId());
        loanService.createLoan(user.getId(), book3.getId());
        loanService.returnBook(user.getId(), book1.getId());

        Loan newLoan = loanService.createLoan(user.getId(), book4.getId());
        assertNotNull(newLoan);
        assertEquals(Loan.STATUS_ACTIVE, newLoan.getStatus());
    }

    // ===================== ESCENARIOS DE ERROR =====================

    @Test
    void shouldThrowExceptionWhenUserNotFoundForLoan() throws BookNotAvialableException {
        Book book = bookService.addBook("Libro", "Autor", 1);

        assertThrows(UserNotFoundException.class,
                () -> loanService.createLoan(9999L, book.getId()));
    }

    @Test
    void shouldThrowExceptionWhenBookNotFoundForLoan() {
        User user = userService.registerUser("Test", "testonly", "pass", "USER");

        assertThrows(BookNotAvialableException.class,
                () -> loanService.createLoan(user.getId(), 9999L));
    }

    @Test
    void shouldThrowExceptionWhenBookNotAvailable()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user1 = userService.registerUser("User 1", "u1test", "pass", "USER");
        User user2 = userService.registerUser("User 2", "u2test", "pass", "USER");
        Book book = bookService.addBook("Libro Único", "Autor", 1);

        loanService.createLoan(user1.getId(), book.getId());

        assertThrows(BookNotAvialableException.class,
                () -> loanService.createLoan(user2.getId(), book.getId()));
    }

    @Test
    void shouldThrowExceptionWhenLoanLimitExceeded()
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        User user = userService.registerUser("Lector Ávido", "lector", "pass", "USER");
        Book book1 = bookService.addBook("L1", "A", 1);
        Book book2 = bookService.addBook("L2", "A", 1);
        Book book3 = bookService.addBook("L3", "A", 1);
        Book book4 = bookService.addBook("L4", "A", 1);

        loanService.createLoan(user.getId(), book1.getId());
        loanService.createLoan(user.getId(), book2.getId());
        loanService.createLoan(user.getId(), book3.getId());

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan(user.getId(), book4.getId()));
    }

    @Test
    void shouldThrowExceptionWhenReturningNonExistentLoan() {
        User user = userService.registerUser("Test", "testreturn", "pass", "USER");

        assertThrows(BookNotAvialableException.class,
                () -> loanService.returnBook(user.getId(), 9999L));
    }

    @Test
    void shouldThrowExceptionWhenReturningWithNonExistentUser() {
        assertThrows(UserNotFoundException.class,
                () -> loanService.returnBook(9999L, 1L));
    }
}
