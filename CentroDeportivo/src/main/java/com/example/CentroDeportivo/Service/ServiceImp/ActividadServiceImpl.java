package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Exception.ReglaNegocioException;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Service.ActividadService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository actividadRepository;

    @Override
    public List<Actividad> listarTodas() {
        return actividadRepository.findAll();
    }

    @Override
    public Actividad obtenerPorId(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró la actividad con ID: " + id
                        )
                );
    }

    @Override
    public Page<Actividad> buscar(
            Long disciplinaId,
            Long entrenadorId,
            LocalDate fecha,
            String nivel,
            Pageable pageable
    ) {

        throw new UnsupportedOperationException(
                "La búsqueda con filtros y paginación aún no está implementada."
        );
    }

    @Override
    public Actividad programar(Actividad datos) {

        if (datos.getHoraInicio() == null ||
                datos.getHoraFin() == null) {

            throw new ReglaNegocioException(
                    "La hora de inicio y la hora de fin son obligatorias."
            );
        }

        if (!datos.getHoraInicio().isBefore(datos.getHoraFin())) {
            throw new ReglaNegocioException(
                    "La hora de inicio debe ser anterior a la hora de fin."
            );
        }

        if (datos.getCupoMaximo() == null ||
                datos.getCupoMaximo() <= 0) {

            throw new ReglaNegocioException(
                    "El cupo máximo debe ser mayor a cero."
            );
        }

        validarSinConflictos(
                datos.getEscenario().getId(),
                datos.getEntrenador().getId(),
                datos.getFecha(),
                datos.getHoraInicio(),
                datos.getHoraFin(),
                null
        );

        datos.setCuposDisponibles(datos.getCupoMaximo());
        datos.setEstado("PROGRAMADA");

        return actividadRepository.save(datos);
    }

    @Override
    public void validarSinConflictos(
            Long escenarioId,
            Long entrenadorId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long actividadIdExcluir
    ) {

    }

    @Override
    public Actividad ocuparCupo(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        if (!tieneCupo(actividadId)) {
            throw new ReglaNegocioException(
                    "La actividad no tiene cupos disponibles."
            );
        }

        actividad.setCuposDisponibles(
                actividad.getCuposDisponibles() - 1
        );

        return actividadRepository.save(actividad);
    }

    @Override
    public Actividad liberarCupo(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        if (actividad.getCuposDisponibles() >= actividad.getCupoMaximo()) {
            throw new ReglaNegocioException(
                    "No hay cupos ocupados para liberar."
            );
        }

        actividad.setCuposDisponibles(
                actividad.getCuposDisponibles() + 1
        );

        return actividadRepository.save(actividad);
    }

    @Override
    public boolean tieneCupo(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        return actividad.getCuposDisponibles() != null &&
                actividad.getCuposDisponibles() > 0;
    }

    @Override
    public Actividad cancelar(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        actividad.setEstado("CANCELADA");

        return actividadRepository.save(actividad);
    }

    @Override
    public Actividad finalizar(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        actividad.setEstado("FINALIZADA");

        return actividadRepository.save(actividad);
    }
}