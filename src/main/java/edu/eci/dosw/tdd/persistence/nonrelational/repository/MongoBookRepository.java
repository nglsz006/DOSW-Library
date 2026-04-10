package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoBookRepository extends MongoRepository<BookDocument, String> {
    Optional<BookDocument> findByTitleAndAuthor(String title, String author);
    List<BookDocument> findByAvailableCopiesGreaterThan(int copies);
    Optional<BookDocument> findByIsbn(String isbn);
}
