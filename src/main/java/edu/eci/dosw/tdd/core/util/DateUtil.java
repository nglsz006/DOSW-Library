package edu.eci.dosw.tdd.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para manejo de fechas en formato ISO (yyyy-MM-dd).
 */
public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {
    }

    public static String getCurrentDate() {
        return LocalDate.now().format(FORMATTER);
    }

    public static String calculateReturnDate(int daysFromNow) {
        return LocalDate.now().plusDays(daysFromNow).format(FORMATTER);
    }

    public static boolean isValidDate(String date) {
        if (date == null || date.isBlank()) {
            return false;
        }
        try {
            LocalDate.parse(date, FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
