package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository actividadRepository;

    @Override
    @Transactional
    public List<Actividad> listarTodas() {
        return actividadRepository.findAll();
    }


    @Override
    @Transactional
    public Actividad obtenerPorId(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró la actividad con ID: " + id
                ));
    }


    @Override
    @Transactional
    public List<Actividad> buscar(
            Long disciplinaId,
            Long entrenadorId,
            LocalDate fecha,
            String nivel) {

        List<Actividad> actividades = actividadRepository.findAll();

        return actividades.stream()
                .filter(a -> disciplinaId == null ||
                        (a.getDisciplina() != null &&
                                a.getDisciplina().getId().equals(disciplinaId)))

                .filter(a -> entrenadorId == null ||
                        (a.getEntrenador() != null &&
                                a.getEntrenador().getId().equals(entrenadorId)))

                .filter(a -> fecha == null ||
                        fecha.equals(a.getFecha()))

                .filter(a -> nivel == null || nivel.isBlank() ||
                        (a.getDisciplina() != null &&
                                a.getDisciplina().getNivel() != null &&
                                a.getDisciplina().getNivel().equalsIgnoreCase(nivel)))

                .toList();
    }


    @Override
    @Transactional
    public Actividad programar(Actividad datos) {

        if (datos.getFecha() == null) {
            throw new RuntimeException("La fecha es obligatoria");
        }

        if (datos.getHoraInicio() == null || datos.getHoraFin() == null) {
            throw new RuntimeException("La hora de inicio y finalización son obligatorias");
        }

        if (!datos.getHoraInicio().isBefore(datos.getHoraFin())) {
            throw new RuntimeException(
                    "La hora de inicio debe ser anterior a la hora de finalización"
            );
        }

        if (datos.getCupoMaximo() == null || datos.getCupoMaximo() <= 0) {
            throw new RuntimeException(
                    "El cupo máximo debe ser mayor que cero"
            );
        }

        if (datos.getEscenario() == null) {
            throw new RuntimeException("El escenario es obligatorio");
        }

        if (datos.getEntrenador() == null) {
            throw new RuntimeException("El entrenador es obligatorio");
        }


        validarSinConflictos(
                datos.getEscenario().getId(),
                datos.getEntrenador().getId(),
                datos.getFecha(),
                datos.getHoraInicio(),
                datos.getHoraFin(),
                datos.getId()
        );


        datos.setCuposDisponibles(datos.getCupoMaximo());


        if (datos.getEstado() == null || datos.getEstado().isBlank()) {
            datos.setEstado("PROGRAMADA");
        }

        return actividadRepository.save(datos);
    }


    @Override
    @Transactional
    public void validarSinConflictos(
            Long escenarioId,
            Long entrenadorId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long actividadIdExcluir) {

        List<Actividad> actividades = actividadRepository.findByFecha(fecha);

        for (Actividad actividad : actividades) {

            // No comparar la actividad consigo misma
            if (actividadIdExcluir != null &&
                    actividad.getId().equals(actividadIdExcluir)) {
                continue;
            }

            // Verificar si existe solapamiento de horarios
            boolean haySolapamiento =
                    horaInicio.isBefore(actividad.getHoraFin()) &&
                            horaFin.isAfter(actividad.getHoraInicio());

            if (!haySolapamiento) {
                continue;
            }

            // Conflicto de escenario
            if (actividad.getEscenario() != null &&
                    actividad.getEscenario().getId().equals(escenarioId)) {

                throw new RuntimeException(
                        "El escenario ya está ocupado en ese horario"
                );
            }

            // Conflicto de entrenador
            if (actividad.getEntrenador() != null &&
                    actividad.getEntrenador().getId().equals(entrenadorId)) {

                throw new RuntimeException(
                        "El entrenador ya tiene una actividad programada en ese horario"
                );
            }
        }
    }


    @Override
    @Transactional
    public Actividad ocuparCupo(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        if (actividad.getCuposDisponibles() <= 0) {
            throw new RuntimeException(
                    "No hay cupos disponibles para esta actividad"
            );
        }

        actividad.setCuposDisponibles(
                actividad.getCuposDisponibles() - 1
        );

        return actividadRepository.save(actividad);
    }


    @Override
    @Transactional
    public Actividad liberarCupo(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        if (actividad.getCuposDisponibles() >= actividad.getCupoMaximo()) {
            throw new RuntimeException(
                    "Todos los cupos ya están disponibles"
            );
        }

        actividad.setCuposDisponibles(
                actividad.getCuposDisponibles() + 1
        );

        return actividadRepository.save(actividad);
    }

    @Override
    @Transactional
    public boolean tieneCupo(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        return actividad.getCuposDisponibles() != null &&
                actividad.getCuposDisponibles() > 0;
    }


    @Override
    @Transactional
    public Actividad cancelar(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        actividad.setEstado("CANCELADA");

        return actividadRepository.save(actividad);
    }


    @Override
    @Transactional
    public Actividad finalizar(Long actividadId) {

        Actividad actividad = obtenerPorId(actividadId);

        actividad.setEstado("FINALIZADA");

        return actividadRepository.save(actividad);
    }
}
