package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        IdGeneratorUtil.getInstance().reset();
    }

    // ===================== ESCENARIOS EXITOSOS =====================

    @Test
    void shouldRegisterUserSuccessfully() {
        User user = userService.registerUser("Juan Pérez");

        assertNotNull(user);
        assertEquals("Juan Pérez", user.getName());
        assertTrue(user.getId() > 0);
    }

    @Test
    void shouldGetAllUsers() {
        userService.registerUser("Usuario A");
        userService.registerUser("Usuario B");

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void shouldGetUserById() {
        User registered = userService.registerUser("María López");

        Optional<User> found = userService.getUserById(registered.getId());
        assertTrue(found.isPresent());
        assertEquals("María López", found.get().getName());
    }

    @Test
    void shouldFindUserByIdSuccessfully() throws UserNotFoundException {
        User registered = userService.registerUser("Carlos");

        User found = userService.findUserById(registered.getId());
        assertEquals("Carlos", found.getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        List<User> users = userService.getAllUsers();
        assertTrue(users.isEmpty());
    }

    // ===================== ESCENARIOS DE ERROR =====================

    @Test
    void shouldReturnEmptyOptionalForNonExistentUserId() {
        Optional<User> result = userService.getUserById(999);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldThrowUserNotFoundExceptionForNonExistentId() {
        assertThrows(UserNotFoundException.class,
                () -> userService.findUserById(999));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringUserWithNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(null));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringUserWithEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(""));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringUserWithBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("   "));
    }
}
