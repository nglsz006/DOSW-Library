package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.exception.BookNotAvialableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    public LoanController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestParam Long userId, @RequestParam Long bookId,
            @AuthenticationPrincipal UserDetails userDetails)
            throws UserNotFoundException, BookNotAvialableException, LoanLimitExceededException {
        if (!isLibrarian(userDetails)) {
            Long currentUserId = resolveCurrentUserId(userDetails);
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        Loan loan = loanService.createLoan(userId, bookId);
        return new ResponseEntity<>(LoanMapper.toDTO(loan), HttpStatus.CREATED);
    }

    @PostMapping("/return")
    public ResponseEntity<LoanDTO> returnBook(@RequestParam Long userId, @RequestParam Long bookId,
            @AuthenticationPrincipal UserDetails userDetails)
            throws BookNotAvialableException, UserNotFoundException {
        if (!isLibrarian(userDetails)) {
            Long currentUserId = resolveCurrentUserId(userDetails);
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        Loan loan = loanService.returnBook(userId, bookId);
        return ResponseEntity.ok(LoanMapper.toDTO(loan));
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<LoanDTO> loans = loanService.getAllLoans().stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<LoanDTO>> getActiveLoans() {
        List<LoanDTO> loans = loanService.getActiveLoans().stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<LoanDTO>> getMyLoans(@AuthenticationPrincipal UserDetails userDetails)
            throws UserNotFoundException {
        Long currentUserId = resolveCurrentUserId(userDetails);
        List<LoanDTO> loans = loanService.getLoansByUserId(currentUserId).stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanDTO>> getLoansByUser(@PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) throws UserNotFoundException {
        if (!isLibrarian(userDetails)) {
            Long currentUserId = resolveCurrentUserId(userDetails);
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        List<LoanDTO> loans = loanService.getLoansByUserId(userId).stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    private boolean isLibrarian(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));
    }

    private Long resolveCurrentUserId(UserDetails userDetails) throws UserNotFoundException {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuario autenticado no encontrado"));
        return user.getId();
    }
}
