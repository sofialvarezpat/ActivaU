package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoListaEspera;
import com.example.CentroDeportivo.Entity.ListaEspera;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ListaEsperaResponse(
        Long id,
        Long actividadId,
        String disciplina,
        LocalDate fechaActividad,
        LocalTime horaInicio,
        Integer posicion,
        EstadoListaEspera estado,
        LocalDateTime fechaIngreso,
        LocalDateTime fechaInvitacion,
        LocalDateTime invitacionExpiraEn) {

    public static ListaEsperaResponse from(ListaEspera e, LocalDateTime invitacionExpiraEn) {
        return new ListaEsperaResponse(
                e.getId(),
                e.getActividad().getId(),
                e.getActividad().getDisciplina().getNombre(),
                e.getActividad().getFecha(),
                e.getActividad().getHoraInicio(),
                e.getPosicion(), e.getEstado(), e.getFechaIngreso(), e.getFechaInvitacion(),
                invitacionExpiraEn);
    }
}
