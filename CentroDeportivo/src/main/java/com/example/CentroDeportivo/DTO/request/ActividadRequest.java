package com.example.CentroDeportivo.DTO.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


public record ActividadRequest (
        @NotNull Long disciplinaId,
        @NotNull Long entrenadorId,
        @NotNull Long escenarioId,
        @NotNull LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin,
        @NotNull @Min(1) Integer cupoMaximo,
        @NotNull @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    BigDecimal precio){
}