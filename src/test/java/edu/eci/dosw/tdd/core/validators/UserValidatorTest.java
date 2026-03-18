package edu.eci.dosw.tdd.core.validators;

import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    @Test
    void shouldPassValidationForValidUser() {
        User user = new User("Nombre", 1);
        assertDoesNotThrow(() -> UserValidator.validate(user));
    }

    @Test
    void shouldThrowExceptionForNullUser() {
        assertThrows(IllegalArgumentException.class, () -> UserValidator.validate(null));
    }

    @Test
    void shouldThrowExceptionForEmptyName() {
        User user = new User("", 1);
        assertThrows(IllegalArgumentException.class, () -> UserValidator.validate(user));
    }

    @Test
    void shouldThrowExceptionForBlankName() {
        User user = new User("   ", 1);
        assertThrows(IllegalArgumentException.class, () -> UserValidator.validate(user));
    }
}
