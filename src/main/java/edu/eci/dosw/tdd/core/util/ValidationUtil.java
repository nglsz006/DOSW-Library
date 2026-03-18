package edu.eci.dosw.tdd.core.util;

/**
 * Utilidad general de validaciones reutilizables.
 */
public class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }
}
