package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanHistory;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanHistoryEmbeddable;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoanPersistenceMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LoanPersistenceMapper() {}

    public static Loan toDomain(LoanEntity entity) {
        List<LoanHistory> history = entity.getHistory() != null
                ? entity.getHistory().stream()
                        .map(h -> LoanHistory.builder()
                                .status(h.getStatus())
                                .changedAt(h.getChangedAt())
                                .build())
                        .collect(Collectors.toList())
                : new ArrayList<>();

        return Loan.builder()
                .book(BookPersistenceMapper.toDomain(entity.getBook()))
                .user(UserPersistenceMapper.toDomain(entity.getUser()))
                .loanDate(entity.getLoanDate().format(FORMATTER))
                .returnDate(entity.getReturnDate() != null ? entity.getReturnDate().format(FORMATTER) : null)
                .status(entity.getStatus())
                .history(history)
                .build();
    }

    public static LoanEntity toEntity(Loan loan, BookEntity bookEntity, UserEntity userEntity) {
        List<LoanHistoryEmbeddable> history = loan.getHistory() != null
                ? loan.getHistory().stream()
                        .map(h -> new LoanHistoryEmbeddable(h.getStatus(), h.getChangedAt()))
                        .collect(Collectors.toList())
                : new ArrayList<>();

        return LoanEntity.builder()
                .book(bookEntity)
                .user(userEntity)
                .loanDate(LocalDate.parse(loan.getLoanDate(), FORMATTER))
                .returnDate(loan.getReturnDate() != null ? LocalDate.parse(loan.getReturnDate(), FORMATTER) : null)
                .status(loan.getStatus())
                .history(history)
                .build();
    }
}
