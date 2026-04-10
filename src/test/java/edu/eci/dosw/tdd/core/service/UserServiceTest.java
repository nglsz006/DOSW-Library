package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // ===================== ESCENARIOS EXITOSOS =====================

    @Test
    void shouldRegisterUserSuccessfully() {
        User user = userService.registerUser("Juan Pérez", "juan", "secret123", "USER");

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("Juan Pérez", user.getName());
        assertEquals("juan", user.getUsername());
        assertEquals("USER", user.getRole());
    }

    @Test
    void shouldGetAllUsers() {
        userService.registerUser("Usuario A", "usera", "pass1", "USER");
        userService.registerUser("Usuario B", "userb", "pass2", "USER");

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void shouldFindUserByIdSuccessfully() throws UserNotFoundException {
        User registered = userService.registerUser("Carlos", "carlos", "pass", "USER");

        User found = userService.findUserById(registered.getId());
        assertEquals("Carlos", found.getName());
        assertEquals("carlos", found.getUsername());
    }

    @Test
    void shouldFindUserByUsername() {
        userService.registerUser("María López", "maria", "pass", "USER");

        Optional<User> found = userService.findByUsername("maria");
        assertTrue(found.isPresent());
        assertEquals("María López", found.get().getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        List<User> users = userService.getAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldRegisterLibrarianWithCorrectRole() {
        User librarian = userService.registerUser("Admin", "admin", "admin123", "LIBRARIAN");

        assertEquals("LIBRARIAN", librarian.getRole());
    }

    @Test
    void shouldPersistUserAndRetrieveById() throws UserNotFoundException {
        User saved = userService.registerUser("Persistente", "persist", "pass", "USER");
        Long id = saved.getId();

        User found = userService.findUserById(id);
        assertEquals(id, found.getId());
        assertEquals("Persistente", found.getName());
    }

    // ===================== ESCENARIOS DE ERROR =====================

    @Test
    void shouldThrowUserNotFoundExceptionForNonExistentId() {
        assertThrows(UserNotFoundException.class,
                () -> userService.findUserById(9999L));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringUserWithNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(null, "user", "pass", "USER"));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringUserWithEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("", "user", "pass", "USER"));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringUserWithBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("   ", "user", "pass", "USER"));
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        userService.registerUser("Primero", "duplicado", "pass", "USER");

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("Segundo", "duplicado", "pass", "USER"));
    }
}
