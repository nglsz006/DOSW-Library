package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoanPersistenceMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LoanPersistenceMapper() {}

    public static Loan toDomain(LoanEntity entity) {
        return new Loan(
                BookPersistenceMapper.toDomain(entity.getBook()),
                UserPersistenceMapper.toDomain(entity.getUser()),
                entity.getLoanDate().format(FORMATTER),
                entity.getReturnDate() != null ? entity.getReturnDate().format(FORMATTER) : null,
                entity.getStatus()
        );
    }

    public static LoanEntity toEntity(Loan loan, BookEntity bookEntity, UserEntity userEntity) {
        return LoanEntity.builder()
                .book(bookEntity)
                .user(userEntity)
                .loanDate(LocalDate.parse(loan.getLoanDate(), FORMATTER))
                .returnDate(loan.getReturnDate() != null ? LocalDate.parse(loan.getReturnDate(), FORMATTER) : null)
                .status(loan.getStatus())
                .build();
    }
}
