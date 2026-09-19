package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoListaEspera;
import com.example.CentroDeportivo.Entity.ListaEspera;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Exception.ReglaNegocioException;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Repository.AfiliadoRepository;
import com.example.CentroDeportivo.Repository.ListaEsperaRepository;
import com.example.CentroDeportivo.Service.ListaEsperaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ListaEsperaServiceImp implements ListaEsperaService {

    private final ListaEsperaRepository listaEsperaRepository;
    private final AfiliadoRepository afiliadoRepository;
    private final ActividadRepository actividadRepository;

    @Override
    @Transactional
    public List<ListaEspera> listarPorActividad(Long actividadId) {
        return listaEsperaRepository.findByActividadIdOrderByPosicionAsc(actividadId);
    }

    @Override
    @Transactional
    public ListaEspera inscribir(Long afiliadoId, Long actividadId) {
        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Afiliado no encontrado: " + afiliadoId));
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada: " + actividadId));

        Optional<Integer> maxPosicion = listaEsperaRepository.findMaxPosicionByActividadId(actividadId);
        Integer siguientePosicion = maxPosicion.map(p -> p + 1).orElse(1);

        ListaEspera listaEspera = new ListaEspera();
        listaEspera.setAfiliado(afiliado);
        listaEspera.setActividad(actividad);
        listaEspera.setPosicion(siguientePosicion);
        listaEspera.setFechaIngreso(LocalDateTime.now());
        listaEspera.setEstado(EstadoListaEspera.EN_ESPERA);

        return listaEsperaRepository.save(listaEspera);
    }

    @Override
    @Transactional
    public Optional<ListaEspera> invitarSiguiente(Long actividadId) {
        Optional<ListaEspera> siguiente = listaEsperaRepository
                .findFirstByActividadIdAndEstadoOrderByPosicionAsc(actividadId, EstadoListaEspera.EN_ESPERA);

        siguiente.ifPresent(le -> {
            le.setEstado(EstadoListaEspera.INVITADO);
            le.setFechaInvitacion(LocalDateTime.now());
            listaEsperaRepository.save(le);
        });

        return siguiente;
    }

    // Marca la invitación como ACEPTADO. NO borra el registro
    // confirmarDesdeListaEspera lo usa para crear la Reserva real y luego
    // es quien decide si conviene conservarlo como historial o eliminarlo.
    @Override
    @Transactional
    public ListaEspera confirmarInvitacion(Long listaEsperaId) {
        ListaEspera listaEspera = obtenerPorId(listaEsperaId);

        if (listaEspera.getEstado() != EstadoListaEspera.INVITADO) {
            throw new ReglaNegocioException(
                    "La invitación no está vigente para confirmar (estado actual: " + listaEspera.getEstado() + ")");
        }

        listaEspera.setEstado(EstadoListaEspera.ACEPTADO);
        return listaEsperaRepository.save(listaEspera);
    }

    @Override
    @Transactional
    public ListaEspera expirarInvitacion(Long listaEsperaId) {
        ListaEspera listaEspera = obtenerPorId(listaEsperaId);
        listaEspera.setEstado(EstadoListaEspera.EXPIRADO);
        listaEsperaRepository.save(listaEspera);

        //  al expirar, se invita automáticamente al siguiente en la fila
        invitarSiguiente(listaEspera.getActividad().getId());

        return listaEspera;
    }

    @Override
    @Transactional
    public ListaEspera obtenerPorId(Long id) {
        return listaEsperaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de lista de espera no encontrado: " + id));
    }
}