package edu.eci.dosw.tdd.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void shouldDetectNullAsEmpty() {
        assertTrue(ValidationUtil.isNullOrEmpty(null));
    }

    @Test
    void shouldDetectBlankAsEmpty() {
        assertTrue(ValidationUtil.isNullOrEmpty("   "));
    }

    @Test
    void shouldDetectEmptyStringAsEmpty() {
        assertTrue(ValidationUtil.isNullOrEmpty(""));
    }

    @Test
    void shouldDetectValidStringAsNotEmpty() {
        assertFalse(ValidationUtil.isNullOrEmpty("texto"));
    }

    @Test
    void shouldDetectPositiveNumber() {
        assertTrue(ValidationUtil.isPositive(5));
    }

    @Test
    void shouldDetectZeroAsNotPositive() {
        assertFalse(ValidationUtil.isPositive(0));
    }

    @Test
    void shouldDetectNegativeAsNotPositive() {
        assertFalse(ValidationUtil.isPositive(-1));
    }
}
