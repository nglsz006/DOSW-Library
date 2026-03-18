package edu.eci.dosw.tdd.core.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorUtilTest {

    @BeforeEach
    void setUp() {
        IdGeneratorUtil.getInstance().reset();
    }

    @Test
    void shouldGenerateIncrementalBookIds() {
        assertEquals(1, IdGeneratorUtil.getInstance().generateBookId());
        assertEquals(2, IdGeneratorUtil.getInstance().generateBookId());
        assertEquals(3, IdGeneratorUtil.getInstance().generateBookId());
    }

    @Test
    void shouldGenerateIncrementalUserIds() {
        assertEquals(1, IdGeneratorUtil.getInstance().generateUserId());
        assertEquals(2, IdGeneratorUtil.getInstance().generateUserId());
    }

    @Test
    void shouldResetCounters() {
        IdGeneratorUtil.getInstance().generateBookId();
        IdGeneratorUtil.getInstance().generateUserId();
        IdGeneratorUtil.getInstance().reset();

        assertEquals(1, IdGeneratorUtil.getInstance().generateBookId());
        assertEquals(1, IdGeneratorUtil.getInstance().generateUserId());
    }

    @Test
    void shouldReturnSameInstance() {
        IdGeneratorUtil instance1 = IdGeneratorUtil.getInstance();
        IdGeneratorUtil instance2 = IdGeneratorUtil.getInstance();
        assertSame(instance1, instance2);
    }
}
