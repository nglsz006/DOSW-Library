package edu.eci.dosw.tdd.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private Long id;
    @NotBlank(message = "El título es obligatorio")
    private String title;
    @NotBlank(message = "El autor es obligatorio")
    private String author;
    @Min(value = 1, message = "El total de copias debe ser mayor a 0")
    private int totalCopies;
    @Min(value = 0, message = "Las copias disponibles no pueden ser negativas")
    private int availableCopies;
}
