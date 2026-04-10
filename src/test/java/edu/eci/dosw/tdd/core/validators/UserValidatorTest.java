package edu.eci.dosw.tdd.core.validators;

import edu.eci.dosw.tdd.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    @Test
    void shouldPassValidationForValidUser() {
        User user = new User(1L, "Nombre", null, null);
        assertDoesNotThrow(() -> UserValidator.validate(user));
    }

    @Test
    void shouldThrowExceptionForNullUser() {
        assertThrows(IllegalArgumentException.class, () -> UserValidator.validate(null));
    }

    @Test
    void shouldThrowExceptionForEmptyName() {
        User user = new User(1L, "", null, null);
        assertThrows(IllegalArgumentException.class, () -> UserValidator.validate(user));
    }

    @Test
    void shouldThrowExceptionForBlankName() {
        User user = new User(1L, "   ", null, null);
        assertThrows(IllegalArgumentException.class, () -> UserValidator.validate(user));
    }
}
