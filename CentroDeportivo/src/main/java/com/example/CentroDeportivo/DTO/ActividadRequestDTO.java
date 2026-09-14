package com.example.CentroDeportivo.DTO;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ActividadRequestDTO {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo debe ser al menos 1")
    private Integer cupoMaximo;

    @NotNull(message = "La disciplina es obligatoria")
    private Long disciplinaId;

    @NotNull(message = "El entrenador es obligatorio")
    private Long entrenadorId;

    @NotNull(message = "El escenario es obligatorio")
    private Long escenarioId;
}