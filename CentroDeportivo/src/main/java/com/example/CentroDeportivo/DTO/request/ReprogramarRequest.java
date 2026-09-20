package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReprogramarRequest(
        @NotNull LocalDate nuevaFecha,
        @NotNull LocalTime nuevaHoraInicio,
        @NotNull LocalTime nuevaHoraFin,
        @NotBlank @Size(max = 300) String motivo) {
}
