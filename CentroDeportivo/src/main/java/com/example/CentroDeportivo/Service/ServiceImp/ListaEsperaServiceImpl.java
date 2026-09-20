package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.response.ListaEsperaResponse;
import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoListaEspera;
import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;
import com.example.CentroDeportivo.Entity.ListaEspera;
import com.example.CentroDeportivo.Exception.BusinessRuleException;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Repository.ListaEsperaRepository;
import com.example.CentroDeportivo.Repository.ReservaRepository;
import com.example.CentroDeportivo.Service.ListaEsperaService;
import com.example.CentroDeportivo.Service.PenalizacionService;
import com.example.CentroDeportivo.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListaEsperaServiceImpl implements ListaEsperaService {

    private static final List<EstadoReserva> RESERVA_ACTIVA = List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);
    private static final List<EstadoListaEspera> EN_FILA = List.of(EstadoListaEspera.EN_ESPERA, EstadoListaEspera.INVITADO);

    private final ListaEsperaRepository listaEsperaRepository;
    private final ActividadRepository actividadRepository;
    private final ReservaRepository reservaRepository;
    private final PenalizacionService penalizacionService;
    private final UsuarioActual usuarioActual;
    private final Clock clock;

    @Value("${app.lista-espera.horas-invitacion:2}")
    private long horasInvitacion;

    @Override
    @Transactional
    public ListaEsperaResponse unirse(Long actividadId) {
        Afiliado afiliado = usuarioActual.afiliadoActual();
        penalizacionService.verificarSinPenalizacion(afiliado);

        Actividad actividad = actividadRepository.findByIdForUpdate(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
        if (actividad.getEstado() == EstadoActividad.CANCELADA) {
            throw new BusinessRuleException("La actividad fue cancelada");
        }
        if (!LocalDateTime.of(actividad.getFecha(), actividad.getHoraInicio()).isAfter(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("La actividad ya inició o finalizó");
        }
        if (actividad.getCuposDisponibles() > 0) {
            throw new BusinessRuleException("La actividad aún tiene cupos disponibles; reserve directamente");
        }
        if (reservaRepository.existsByAfiliadoIdAndActividadIdAndEstadoIn(afiliado.getId(), actividadId, RESERVA_ACTIVA)) {
            throw new ConflictException("Ya tiene una reserva para esta actividad");
        }
        if (listaEsperaRepository.existsByActividadIdAndAfiliadoIdAndEstadoIn(actividadId, afiliado.getId(), EN_FILA)) {
            throw new ConflictException("Ya está en la lista de espera de esta actividad");
        }

        ListaEspera entrada = new ListaEspera();
        entrada.setActividad(actividad);
        entrada.setAfiliado(afiliado);
        entrada.setPosicion(listaEsperaRepository.maxPosicion(actividadId) + 1);
        entrada.setEstado(EstadoListaEspera.EN_ESPERA);
        return toResponse(listaEsperaRepository.save(entrada));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListaEsperaResponse> mias() {
        Afiliado afiliado = usuarioActual.afiliadoActual();
        return listaEsperaRepository.findByAfiliadoIdOrderByFechaIngresoDesc(afiliado.getId())
                .stream().map(this::toResponse).toList();
    }

    private ListaEsperaResponse toResponse(ListaEspera e) {
        LocalDateTime expira = (e.getEstado() == EstadoListaEspera.INVITADO && e.getFechaInvitacion() != null)
                ? e.getFechaInvitacion().plusHours(horasInvitacion) : null;
        return ListaEsperaResponse.from(e, expira);
    }
}
