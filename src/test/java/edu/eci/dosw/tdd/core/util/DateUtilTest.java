package edu.eci.dosw.tdd.core.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void shouldReturnCurrentDate() {
        String today = DateUtil.getCurrentDate();
        String expected = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertEquals(expected, today);
    }

    @Test
    void shouldCalculateReturnDate() {
        String returnDate = DateUtil.calculateReturnDate(14);
        String expected = LocalDate.now().plusDays(14).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertEquals(expected, returnDate);
    }

    @Test
    void shouldValidateCorrectDate() {
        assertTrue(DateUtil.isValidDate("2025-06-15"));
    }

    @Test
    void shouldRejectInvalidDate() {
        assertFalse(DateUtil.isValidDate("no-es-fecha"));
    }

    @Test
    void shouldRejectNullDate() {
        assertFalse(DateUtil.isValidDate(null));
    }

    @Test
    void shouldRejectEmptyDate() {
        assertFalse(DateUtil.isValidDate(""));
    }
}
