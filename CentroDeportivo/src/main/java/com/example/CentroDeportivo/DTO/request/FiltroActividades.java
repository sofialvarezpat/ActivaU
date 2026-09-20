package com.example.CentroDeportivo.DTO.request;


import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import com.example.CentroDeportivo.Entity.Enum.NivelDisciplina;

import java.time.LocalDate;

/** Filtros de búsqueda de RF04. Todos son opcionales. */
public record FiltroActividades(
        String disciplina,
        Long entrenadorId,
        LocalDate fecha,
        NivelDisciplina nivel,
        EstadoActividad estado,
        boolean incluirPasadas) {
}
