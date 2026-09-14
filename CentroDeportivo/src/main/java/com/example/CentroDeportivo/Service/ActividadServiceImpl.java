package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Actividad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

    @Override
    public List<Actividad> listarTodas() {
        return List.of();
    }

    @Override
    public Actividad obtenerPorId(Long id) {
        return null;
    }

    @Override
    public List<Actividad> buscar(Long disciplinaId, Long entrenadorId, LocalDate fecha, String nivel) {
        return List.of();
    }

    @Override
    public Actividad programar(Actividad datos) {
        return null;
    }

    @Override
    public void validarSinConflictos(Long escenarioId, Long entrenadorId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Long actividadIdExcluir) {

    }

    @Override
    public Actividad ocuparCupo(Long actividadId) {
        return null;
    }

    @Override
    public Actividad liberarCupo(Long actividadId) {
        return null;
    }

    @Override
    public boolean tieneCupo(Long actividadId) {
        return false;
    }

    @Override
    public Actividad cancelar(Long actividadId) {
        return null;
    }

    @Override
    public Actividad finalizar(Long actividadId) {
        return null;
    }
}
