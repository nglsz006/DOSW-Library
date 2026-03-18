package edu.eci.dosw.tdd.core.validators;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.ValidationUtil;

/**
 * Patrón Strategy: encapsula la lógica de validación de usuarios.
 */
public class UserValidator {

    public static void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (ValidationUtil.isNullOrEmpty(user.getName())) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        }
    }
}
