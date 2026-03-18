package edu.eci.dosw.tdd.core.validators;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.ValidationUtil;

/**
 * Patrón Strategy: encapsula la lógica de validación de libros.
 * Valida que los campos obligatorios estén presentes y sean coherentes.
 */
public class BookValidator {

    public static void validate(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        if (ValidationUtil.isNullOrEmpty(book.getTitle())) {
            throw new IllegalArgumentException("El título del libro no puede estar vacío");
        }
        if (ValidationUtil.isNullOrEmpty(book.getAuthor())) {
            throw new IllegalArgumentException("El autor del libro no puede estar vacío");
        }
    }
}
