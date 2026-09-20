package com.example.CentroDeportivo.DTO.response;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import com.example.CentroDeportivo.Entity.Enum.NivelDisciplina;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ActividadResponse(
        Long id,
        Long disciplinaId,
        String disciplina,
        NivelDisciplina nivel,
        Long entrenadorId,
        String entrenador,
        Long escenarioId,
        String escenario,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cupoMaximo,
        Integer cuposDisponibles,
        BigDecimal precio,
        EstadoActividad estado) {

    public static ActividadResponse from(Actividad a) {
        return new ActividadResponse(
                a.getId(),
                a.getDisciplina().getId(), a.getDisciplina().getNombre(), a.getDisciplina().getNivel(),
                a.getEntrenador().getId(), a.getEntrenador().getNombres() + " " + a.getEntrenador().getApellidos(),
                a.getEscenario().getId(), a.getEscenario().getNombre(),
                a.getFecha(), a.getHoraInicio(), a.getHoraFin(),
                a.getCupoMaximo(), a.getCuposDisponibles(), a.getPrecio(), a.getEstado());
    }
}
