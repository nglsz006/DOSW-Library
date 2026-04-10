package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanEmbedded;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;

public class UserDocumentMapper {

    private UserDocumentMapper() {}

    public static User toDomain(UserDocument doc) {
        return User.builder()
                .id(null)
                .name(doc.getName())
                .username(doc.getUsername())
                .role(doc.getRole())
                .email(doc.getEmail())
                .membershipType(doc.getMembershipType())
                .registrationDate(doc.getRegistrationDate() != null
                        ? java.time.LocalDate.parse(doc.getRegistrationDate()) : null)
                .build();
    }

    public static UserDocument toDocument(User user) {
        return UserDocument.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole())
                .email(user.getEmail())
                .membershipType(user.getMembershipType())
                .registrationDate(user.getRegistrationDate() != null
                        ? user.getRegistrationDate().toString() : null)
                .build();
    }

    public static LoanEmbedded toLoanEmbedded(Loan loan) {
        return LoanEmbedded.builder()
                .bookId(loan.getBook() != null && loan.getBook().getId() != null
                        ? loan.getBook().getId().toString() : null)
                .loanDate(loan.getLoanDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .build();
    }

    public static Loan loanEmbeddedToDomain(LoanEmbedded embedded, Book book, User user) {
        return Loan.builder()
                .book(book)
                .user(user)
                .loanDate(embedded.getLoanDate())
                .returnDate(embedded.getReturnDate())
                .status(embedded.getStatus())
                .build();
    }
}
