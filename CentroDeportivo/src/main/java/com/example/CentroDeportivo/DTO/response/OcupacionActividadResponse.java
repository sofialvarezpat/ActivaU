package com.example.CentroDeportivo.DTO.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record OcupacionActividadResponse(
        Long actividadId,
        String disciplina,
        String escenario,
        LocalDate fecha,
        LocalTime horaInicio,
        Integer cupoMaximo,
        long reservas,
        double porcentajeOcupacion) {
}
