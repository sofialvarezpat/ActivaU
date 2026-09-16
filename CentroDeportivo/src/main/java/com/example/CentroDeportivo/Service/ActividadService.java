package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Actividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ActividadService {

    //Busqueda Normal
    List<Actividad> listarTodas();

    Actividad obtenerPorId(Long id);

    //El requerimiento exige "filtros y paginaciion" en la
    //busqueda de actividades
    Page<Actividad> buscar(Long disciplinaId, Long entrenadorId, LocalDate fecha, String nivel, Pageable pageable);

    Actividad programar(Actividad datos);

    //Evita conflictos de horario en el mismo escenario o con el mismo entrenador
    void validarSinConflictos(Long escenarioId, Long entrenadorId, LocalDate fecha,
                              LocalTime horaInicio, LocalTime horaFin, Long actividadIdExcluir);

    Actividad ocuparCupo(Long actividadId);

    Actividad liberarCupo(Long actividadId);

    //Boolean porque tiene cupo o no
    boolean tieneCupo(Long actividadId);

    Actividad cancelar(Long actividadId);

    Actividad finalizar(Long actividadId);
}