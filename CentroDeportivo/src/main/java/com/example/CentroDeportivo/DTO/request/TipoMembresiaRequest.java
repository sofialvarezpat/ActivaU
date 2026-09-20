package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TipoMembresiaRequest(
        @NotBlank @Size(max = 100)
        String nombre,

        @NotNull @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
        BigDecimal precio,

        @NotNull @Min(1)
        Integer duracionDias,

        @Size(max = 500)
        String beneficios) {
}
