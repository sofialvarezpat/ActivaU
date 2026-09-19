package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import com.example.CentroDeportivo.Entity.Enum.NivelDisciplina;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Exception.ReglaNegocioException;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la actividad con ID: " + id));
    }

    @Override
    @Transactional
    public Page<Actividad> buscar(Long disciplinaId, Long entrenadorId, LocalDate fecha, String nivel, Pageable pageable) {
        NivelDisciplina nivelEnum = parsearNivel(nivel);
        return actividadRepository.buscarConFiltros(disciplinaId, entrenadorId, fecha, nivelEnum, pageable);
    }

    @Override
    @Transactional
    public List<Actividad> buscar(Long disciplinaId, Long entrenadorId, LocalDate fecha, String nivel) {
        NivelDisciplina nivelEnum = parsearNivel(nivel);
        return actividadRepository.findAll().stream()
                .filter(a -> disciplinaId == null || (a.getDisciplina() != null && a.getDisciplina().getId().equals(disciplinaId)))
                .filter(a -> entrenadorId == null || (a.getEntrenador() != null && a.getEntrenador().getId().equals(entrenadorId)))
                .filter(a -> fecha == null || fecha.equals(a.getFecha()))
                .filter(a -> nivelEnum == null || (a.getDisciplina() != null && nivelEnum.equals(a.getDisciplina().getNivel())))
                .toList();
    }

    private NivelDisciplina parsearNivel(String nivel) {
        if (nivel == null || nivel.isBlank()) return null;
        try {
            return NivelDisciplina.valueOf(nivel.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReglaNegocioException("Nivel inválido: " + nivel);
        }
    }

    @Override
    @Transactional
    public Actividad programar(Actividad datos) {
        if (datos.getFecha() == null) throw new ReglaNegocioException("La fecha es obligatoria");

        if (datos.getHoraInicio() == null || datos.getHoraFin() == null)
            throw new ReglaNegocioException("La hora de inicio y finalización son obligatorias");

        if (!datos.getHoraInicio().isBefore(datos.getHoraFin()))
            throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de finalización");

        if (datos.getFecha().isBefore(LocalDate.now()))
            throw new ReglaNegocioException("No se puede programar una actividad en una fecha pasada");

        if (datos.getCupoMaximo() == null || datos.getCupoMaximo() <= 0)
            throw new ReglaNegocioException("El cupo máximo debe ser mayor que cero");

        if (datos.getEscenario() == null) throw new ReglaNegocioException("El escenario es obligatorio");

        if (datos.getEntrenador() == null) throw new ReglaNegocioException("El entrenador es obligatorio");

        validarSinConflictos(datos.getEscenario().getId(), datos.getEntrenador().getId(),
                datos.getFecha(), datos.getHoraInicio(), datos.getHoraFin(), datos.getId());

        datos.setCuposDisponibles(datos.getCupoMaximo());
        if (datos.getEstado() == null) {
            datos.setEstado(EstadoActividad.PROGRAMADA);
        }
        return actividadRepository.save(datos);
    }

    @Override
    @Transactional
    public void validarSinConflictos(Long escenarioId, Long entrenadorId, LocalDate fecha,
                                     LocalTime horaInicio, LocalTime horaFin, Long actividadIdExcluir) {
        List<Actividad> actividades = actividadRepository.findByFecha(fecha);
        for (Actividad actividad : actividades) {
            if (actividadIdExcluir != null && actividad.getId().equals(actividadIdExcluir)) continue;

            boolean haySolapamiento = horaInicio.isBefore(actividad.getHoraFin()) && horaFin.isAfter(actividad.getHoraInicio());
            if (!haySolapamiento) continue;

            if (actividad.getEscenario() != null && actividad.getEscenario().getId().equals(escenarioId))
                throw new ReglaNegocioException("El escenario ya está ocupado en ese horario");
            if (actividad.getEntrenador() != null && actividad.getEntrenador().getId().equals(entrenadorId))
                throw new ReglaNegocioException("El entrenador ya tiene una actividad programada en ese horario");
        }
    }

    @Override
    @Transactional
    public Actividad ocuparCupo(Long actividadId) {
        Actividad actividad = obtenerPorId(actividadId);
        if (actividad.getCuposDisponibles() <= 0)
            throw new ReglaNegocioException("No hay cupos disponibles para esta actividad");
        actividad.setCuposDisponibles(actividad.getCuposDisponibles() - 1);
        return actividadRepository.save(actividad);
    }

    @Override
    @Transactional
    public Actividad liberarCupo(Long actividadId) {
        Actividad actividad = obtenerPorId(actividadId);
        if (actividad.getCuposDisponibles() >= actividad.getCupoMaximo())
            throw new ReglaNegocioException("Todos los cupos ya están disponibles");
        actividad.setCuposDisponibles(actividad.getCuposDisponibles() + 1);
        return actividadRepository.save(actividad);
    }

    @Override
    @Transactional
    public boolean tieneCupo(Long actividadId) {
        Actividad actividad = obtenerPorId(actividadId);
        return actividad.getCuposDisponibles() != null && actividad.getCuposDisponibles() > 0;
    }

    @Override
    @Transactional
    public Actividad cancelar(Long actividadId) {
        Actividad actividad = obtenerPorId(actividadId);
        actividad.setEstado(EstadoActividad.CANCELADA);
        return actividadRepository.save(actividad);
    }

    @Override
    @Transactional
    public Actividad finalizar(Long actividadId) {
        Actividad actividad = obtenerPorId(actividadId);

        if (actividad.getEstado() == EstadoActividad.CANCELADA) {
            throw new ReglaNegocioException("No se puede finalizar una actividad ya cancelada");
        }
        if (actividad.getEstado() == EstadoActividad.FINALIZADA) {
            throw new ReglaNegocioException("La actividad ya está finalizada");
        }

        actividad.setEstado(EstadoActividad.FINALIZADA);
        return actividadRepository.save(actividad);
    }
}