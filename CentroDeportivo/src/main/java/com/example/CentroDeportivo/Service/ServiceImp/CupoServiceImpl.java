package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoListaEspera;
import com.example.CentroDeportivo.Entity.ListaEspera;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Repository.ListaEsperaRepository;
import com.example.CentroDeportivo.Service.CupoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Manejo de cupos y lista de espera (RF06). Regla clave: un cupo liberado NO vuelve al público
 * mientras haya alguien en la fila; queda "retenido" para el primero (FIFO) hasta que acepte o expire.
 */
@Slf4j
@Service
public class CupoServiceImpl implements CupoService {

    private final ListaEsperaRepository listaEsperaRepository;
    private final ActividadRepository actividadRepository;
    private final Clock clock;
    private final long horasInvitacion;

    public CupoServiceImpl(ListaEsperaRepository listaEsperaRepository,
                           ActividadRepository actividadRepository,
                           Clock clock,
                           @Value("${app.lista-espera.horas-invitacion:2}") long horasInvitacion) {
        this.listaEsperaRepository = listaEsperaRepository;
        this.actividadRepository = actividadRepository;
        this.clock = clock;
        this.horasInvitacion = horasInvitacion;
    }

    @Override
    @Transactional
    public void liberarCupo(Actividad actividad) {
        LocalDateTime ahora = LocalDateTime.now(clock);
        boolean vigente = actividad.getEstado() != EstadoActividad.CANCELADA
                && LocalDateTime.of(actividad.getFecha(), actividad.getHoraInicio()).isAfter(ahora);

        Optional<ListaEspera> siguiente = vigente
                ? listaEsperaRepository.findFirstByActividadIdAndEstadoOrderByPosicionAsc(actividad.getId(), EstadoListaEspera.EN_ESPERA)
                : Optional.empty();

        if (siguiente.isPresent()) {
            ListaEspera entrada = siguiente.get();
            entrada.setEstado(EstadoListaEspera.INVITADO);
            entrada.setFechaInvitacion(ahora);
            listaEsperaRepository.save(entrada);
            log.info("Invitación (simulada) enviada al afiliado {} para la actividad {}; vence en {} h",
                    entrada.getAfiliado().getId(), actividad.getId(), horasInvitacion);
        } else {
            actividad.setCuposDisponibles(actividad.getCuposDisponibles() + 1);
            actividadRepository.save(actividad);
        }
    }

    @Override
    @Transactional
    public void expirarInvitacionesVencidas() {
        LocalDateTime limite = LocalDateTime.now(clock).minusHours(horasInvitacion);
        List<Long> ids = listaEsperaRepository.findIdsVencidos(EstadoListaEspera.INVITADO, limite);
        for (Long id : ids) {
            Optional<Long> actividadId = listaEsperaRepository.findActividadIdById(id);
            if (actividadId.isEmpty()) {
                continue;
            }
            // Se bloquea la actividad ANTES de leer la entrada, para no pisarse con un "aceptar" simultáneo.
            Optional<Actividad> actividad = actividadRepository.findByIdForUpdate(actividadId.get());
            Optional<ListaEspera> entrada = listaEsperaRepository.findById(id);
            if (actividad.isEmpty() || entrada.isEmpty() || entrada.get().getEstado() != EstadoListaEspera.INVITADO) {
                continue;
            }
            entrada.get().setEstado(EstadoListaEspera.EXPIRADO);
            listaEsperaRepository.save(entrada.get());
            log.info("Invitación {} expirada; se pasa al siguiente de la fila", id);
            liberarCupo(actividad.get());
        }
    }
}
