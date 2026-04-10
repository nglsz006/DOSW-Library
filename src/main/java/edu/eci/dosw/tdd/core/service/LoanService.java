package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.validators.LoanValidator;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.LoanPersistenceMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaBookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaLoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;
    private static final int LOAN_DAYS = 14;

    private final JpaLoanRepository loanRepository;
    private final JpaBookRepository bookRepository;
    private final JpaUserRepository userRepository;
    private final BookService bookService;

    public LoanService(JpaLoanRepository loanRepository, JpaBookRepository bookRepository,
                       JpaUserRepository userRepository, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
    }

    @Transactional
    public Loan createLoan(Long userId, Long bookId)
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con ID: " + userId));

        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotAvialableException("No se encontró el libro con ID: " + bookId));

        if (bookEntity.getAvailableCopies() == 0) {
            throw new BookNotAvialableException("El libro '" + bookEntity.getTitle() + "' no está disponible");
        }

        long activeLoans = loanRepository.countByUserIdAndStatus(userId, Loan.STATUS_ACTIVE);
        if (activeLoans >= MAX_ACTIVE_LOANS_PER_USER) {
            throw new LoanLimitExceededException(
                    "El usuario '" + userEntity.getName() + "' ha alcanzado el límite de " + MAX_ACTIVE_LOANS_PER_USER + " préstamos activos");
        }

        bookService.decrementAvailableCopies(bookId);

        LoanEntity loanEntity = LoanEntity.builder()
                .book(bookEntity)
                .user(userEntity)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(LOAN_DAYS))
                .status(Loan.STATUS_ACTIVE)
                .build();

        Loan loan = LoanPersistenceMapper.toDomain(loanEntity);
        LoanValidator.validate(loan);

        loanRepository.save(loanEntity);
        return loan;
    }

    @Transactional
    public Loan returnBook(Long userId, Long bookId)
            throws BookNotAvialableException, UserNotFoundException {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con ID: " + userId));

        LoanEntity loanEntity = loanRepository.findByUserIdAndBookIdAndStatus(userId, bookId, Loan.STATUS_ACTIVE)
                .orElseThrow(() -> new BookNotAvialableException(
                        "No se encontró un préstamo activo para el usuario " + userId + " y libro " + bookId));

        loanEntity.setStatus(Loan.STATUS_RETURNED);
        loanEntity.setReturnDate(LocalDate.now());
        loanRepository.save(loanEntity);

        bookService.incrementAvailableCopies(bookId);

        return LoanPersistenceMapper.toDomain(loanEntity);
    }

    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(LoanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Loan> getActiveLoans() {
        return loanRepository.findAll().stream()
                .filter(loan -> Loan.STATUS_ACTIVE.equals(loan.getStatus()))
                .map(LoanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId).stream()
                .map(LoanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
