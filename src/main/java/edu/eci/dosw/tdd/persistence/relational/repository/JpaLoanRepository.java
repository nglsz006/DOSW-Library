package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JpaLoanRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByUserIdAndStatus(Long userId, String status);
    List<LoanEntity> findByUserId(Long userId);
    Optional<LoanEntity> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, String status);
    long countByUserIdAndStatus(Long userId, String status);
}
