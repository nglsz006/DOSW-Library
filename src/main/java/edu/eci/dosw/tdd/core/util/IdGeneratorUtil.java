package edu.eci.dosw.tdd.core.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utilidad para generar IDs únicos autoincrementales.
 * Patrón Singleton para garantizar unicidad global de IDs.
 */
public class IdGeneratorUtil {

    private static final IdGeneratorUtil INSTANCE = new IdGeneratorUtil();
    private final AtomicInteger bookCounter = new AtomicInteger(0);
    private final AtomicInteger userCounter = new AtomicInteger(0);

    private IdGeneratorUtil() {
    }

    public static IdGeneratorUtil getInstance() {
        return INSTANCE;
    }

    public int generateBookId() {
        return bookCounter.incrementAndGet();
    }

    public int generateUserId() {
        return userCounter.incrementAndGet();
    }

    /**
     * Reinicia los contadores (útil para tests).
     */
    public void reset() {
        bookCounter.set(0);
        userCounter.set(0);
    }
}
