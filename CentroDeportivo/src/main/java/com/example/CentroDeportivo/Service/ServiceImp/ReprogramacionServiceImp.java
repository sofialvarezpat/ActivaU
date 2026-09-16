package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.ListaEspera;
import com.example.CentroDeportivo.Entity.Reprogramacion;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Repository.ReprogramacionRepository;
import com.example.CentroDeportivo.Service.ListaEsperaService;
import com.example.CentroDeportivo.Service.ReprogramacionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReprogramacionServiceImp implements ReprogramacionService {

    private static final String ESTADO_REPROGRAMA = "REPROGRAMA";

    private final ReprogramacionRepository reprogramacionRepository;
    private final ActividadRepository actividadRepository;
    private final ListaEsperaService listaEsperaService;

    @Override
    @Transactional
    public List<Reprogramacion> listarPorActividad(Long actividadId) {
        return reprogramacionRepository.findByActividadId(actividadId);
    }

    @Override
    @Transactional
    public Reprogramacion reprogramar(Long actividadId, LocalDate nuevaFecha,
                                      LocalTime nuevaHoraInicio, LocalTime nuevaHoraFin, String motivo) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada: " + actividadId));

        // Guarda el historial ANTES de sobrescribir la actividad
        Reprogramacion reprogramacion = new Reprogramacion();
        reprogramacion.setActividad(actividad);
        reprogramacion.setFechaAnterior(actividad.getFecha());
        reprogramacion.setHoraAnterior(actividad.getHoraInicio());
        reprogramacion.setFechaNueva(nuevaFecha);
        reprogramacion.setHoraNueva(nuevaHoraInicio);
        reprogramacion.setMotivo(motivo);
        reprogramacion.setFechaCambio(LocalDateTime.now());

        //  validar en el nuevo horario antes de aplicar el cambio
        // requiere consultar otras actividades del mismo escenario/entrenador en ese horario

        actividad.setFecha(nuevaFecha);
        actividad.setHoraInicio(nuevaHoraInicio);
        actividad.setHoraFin(nuevaHoraFin);
        actividad.setEstado(ESTADO_REPROGRAMA);
        actividadRepository.save(actividad);

        Reprogramacion guardada = reprogramacionRepository.save(reprogramacion);

        // Según el diagrama: si hay afiliados en lista de espera, notificar el cambio
        List<ListaEspera> enEspera = listaEsperaService.listarPorActividad(actividadId);
        if (!enEspera.isEmpty()) {

        }

        return guardada;
    }

    @Override
    @Transactional
    public Reprogramacion obtenerPorId(Long id) {
        return reprogramacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reprogramación no encontrada: " + id));
    }
}
