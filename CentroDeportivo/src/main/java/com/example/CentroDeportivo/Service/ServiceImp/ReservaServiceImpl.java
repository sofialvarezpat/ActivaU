package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.AceptarInvitacionRequest;
import com.example.CentroDeportivo.DTO.request.DatosPagoRequest;
import com.example.CentroDeportivo.DTO.request.ReservaRequest;
import com.example.CentroDeportivo.DTO.response.CancelacionReservaResponse;
import com.example.CentroDeportivo.DTO.response.PagoResponse;
import com.example.CentroDeportivo.DTO.response.ReservaResponse;
import com.example.CentroDeportivo.Entity.*;
import com.example.CentroDeportivo.Entity.Enum.*;
import com.example.CentroDeportivo.Exception.BusinessRuleException;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.PagoRechazadoException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.*;
import com.example.CentroDeportivo.Service.CupoService;
import com.example.CentroDeportivo.Service.PagoService;
import com.example.CentroDeportivo.Service.PenalizacionService;
import com.example.CentroDeportivo.Service.ReservaService;
import com.example.CentroDeportivo.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Flujo central del sistema (RF05, RF11). Todo lo que toca cupos se hace con la actividad bloqueada
 * (SELECT ... FOR UPDATE) dentro de UNA transacción: así no hay sobreventa (R01) ni cupo descontado
 * si el pago falla (RNF06).
 */
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final List<EstadoReserva> ACTIVAS = List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);
    private static final List<EstadoReserva> CON_ASISTENCIA =
            List.of(EstadoReserva.CONFIRMADA, EstadoReserva.ASISTIDA, EstadoReserva.NO_ASISTIDA);

    private final ReservaRepository reservaRepository;
    private final ActividadRepository actividadRepository;
    private final ListaEsperaRepository listaEsperaRepository;
    private final MembresiaRepository membresiaRepository;
    private final PagoRepository pagoRepository;
    private final PagoService pagoService;
    private final PenalizacionService penalizacionService;
    private final CupoService cupoService;
    private final UsuarioActual usuarioActual;
    private final Clock clock;

    @Value("${app.lista-espera.horas-invitacion:2}")
    private long horasInvitacion;

    // ------------------------------------------------------------------ RF05
    @Override
    @Transactional(noRollbackFor = PagoRechazadoException.class)
    public ReservaResponse reservar(ReservaRequest req) {
        Afiliado afiliado = usuarioActual.resolverAfiliado(req.afiliadoId());
        return crearReserva(afiliado, req.actividadId(), req.membresiaId(), req.datosPago(), OrigenCupo.DIRECTO);
    }

    @Override
    @Transactional(noRollbackFor = PagoRechazadoException.class)
    public ReservaResponse aceptarInvitacion(Long listaEsperaId, AceptarInvitacionRequest req) {
        Afiliado afiliado = usuarioActual.afiliadoActual();

        // Se bloquea la actividad antes de leer la entrada (evita chocar con la expiración automática).
        Long actividadId = listaEsperaRepository.findActividadIdById(listaEsperaId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitación no encontrada"));
        actividadRepository.findByIdForUpdate(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        ListaEspera entrada = listaEsperaRepository.findById(listaEsperaId)
                .filter(e -> e.getAfiliado().getId().equals(afiliado.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Invitación no encontrada"));
        if (entrada.getEstado() != EstadoListaEspera.INVITADO) {
            throw new ConflictException("La invitación ya no está vigente");
        }
        if (entrada.getFechaInvitacion().plusHours(horasInvitacion).isBefore(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("La invitación expiró");
        }

        ReservaResponse respuesta = crearReserva(afiliado, actividadId,
                req == null ? null : req.membresiaId(), req == null ? null : req.datosPago(), OrigenCupo.LISTA_ESPERA);
        entrada.setEstado(EstadoListaEspera.ACEPTADO);
        listaEsperaRepository.save(entrada);
        return respuesta;
    }

    private ReservaResponse crearReserva(Afiliado afiliado, Long actividadId, Long membresiaId,
                                         DatosPagoRequest datosPago, OrigenCupo origen) {
        penalizacionService.verificarSinPenalizacion(afiliado);

        Actividad actividad = actividadRepository.findByIdForUpdate(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
        if (actividad.getEstado() == EstadoActividad.CANCELADA) {
            throw new BusinessRuleException("La actividad fue cancelada");
        }
        if (!LocalDateTime.of(actividad.getFecha(), actividad.getHoraInicio()).isAfter(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("La actividad ya inició o finalizó");
        }
        if (reservaRepository.existsByAfiliadoIdAndActividadIdAndEstadoIn(afiliado.getId(), actividad.getId(), ACTIVAS)) {
            throw new ConflictException("Ya tiene una reserva para esta actividad");
        }
        // Regla de negocio 2: no reservar dos actividades que se traslapen.
        if (reservaRepository.contarTraslape(afiliado.getId(), ACTIVAS, EstadoActividad.CANCELADA,
                actividad.getFecha(), actividad.getHoraInicio(), actividad.getHoraFin()) > 0) {
            throw new ConflictException("Ya tiene otra actividad reservada que se traslapa con este horario");
        }
        // Regla de negocio 1: nunca superar el cupo. (Con invitación de lista de espera el cupo ya estaba retenido.)
        if (origen == OrigenCupo.DIRECTO && actividad.getCuposDisponibles() <= 0) {
            throw new ConflictException("La actividad no tiene cupos disponibles; puede unirse a la lista de espera");
        }

        boolean usaMembresia = membresiaId != null;
        if (usaMembresia && datosPago != null) {
            throw new BusinessRuleException("Indique una membresía o los datos de pago, no ambos");
        }
        boolean gratuita = actividad.getPrecio().compareTo(BigDecimal.ZERO) == 0;
        Membresia membresia = null;
        if (usaMembresia) {
            membresia = validarMembresia(membresiaId, afiliado, actividad);   // regla de negocio 3
        } else if (!gratuita && datosPago == null) {
            throw new BusinessRuleException("Debe indicar una membresía vigente o los datos de pago");
        }

        // El cobro va DESPUÉS de todas las validaciones y ANTES de tocar cupos: si el banco rechaza,
        // se guarda solo el Pago RECHAZADO (noRollbackFor) y no cambia ni la reserva ni el cupo (RNF06).
        Pago pago = null;
        if (!usaMembresia && !gratuita) {
            String concepto = "Reserva actividad #" + actividad.getId() + " - " + actividad.getDisciplina().getNombre();
            pago = pagoService.cobrar(afiliado, actividad.getPrecio(), concepto, datosPago);
        }

        if (origen == OrigenCupo.DIRECTO) {
            actividad.setCuposDisponibles(actividad.getCuposDisponibles() - 1);
            actividadRepository.save(actividad);
        }
        Reserva reserva = new Reserva();
        reserva.setAfiliado(afiliado);
        reserva.setActividad(actividad);
        reserva.setMembresia(membresia);
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setOrigenCupo(origen);
        reserva = reservaRepository.save(reserva);

        if (pago != null) {
            pago.setReserva(reserva); // entidad gestionada: se actualiza al hacer commit
        }
        return toResponse(reserva, pago);
    }

    private Membresia validarMembresia(Long membresiaId, Afiliado afiliado, Actividad actividad) {
        Membresia m = membresiaRepository.findByIdAndAfiliadoId(membresiaId, afiliado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada para este afiliado"));
        LocalDate hoy = LocalDate.now(clock);
        if (m.getEstado() != EstadoMembresia.ACTIVA || hoy.isBefore(m.getFechaInicio()) || hoy.isAfter(m.getFechaFin())) {
            throw new BusinessRuleException("La membresía no está vigente");
        }
        if (actividad.getFecha().isAfter(m.getFechaFin())) {
            throw new BusinessRuleException("La membresía vence antes de la fecha de la actividad");
        }
        return m;
    }

    // ------------------------------------------------------------------ RF11
    @Override
    @Transactional
    public CancelacionReservaResponse cancelar(Long reservaId) {
        Long actividadId = reservaRepository.findActividadIdById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        Actividad actividad = actividadRepository.findByIdForUpdate(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        Usuario actual = usuarioActual.obtener();
        if (actual.getRol() == Rol.AFILIADO && !reserva.getAfiliado().getId().equals(actual.getId())) {
            throw new AccessDeniedException("Solo puede cancelar sus propias reservas");
        }
        if (!ACTIVAS.contains(reserva.getEstado())) {
            throw new ConflictException("La reserva ya estaba cancelada o no puede cancelarse");
        }

        LocalDateTime ahora = LocalDateTime.now(clock);  // hora del servidor, no la que diga el cliente
        LocalDateTime inicio = LocalDateTime.of(actividad.getFecha(), actividad.getHoraInicio());
        if (!inicio.isAfter(ahora)) {
            throw new BusinessRuleException("No se puede cancelar una actividad que ya ocurrió o está en curso");
        }

        boolean tardia = penalizacionService.esCancelacionTardia(ahora, inicio);
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        Penalizacion penalizacion = null;
        if (tardia) {
            penalizacion = penalizacionService.registrarCancelacionTardia(reserva.getAfiliado(), reserva, ahora);
        }
        cupoService.liberarCupo(actividad);

        return new CancelacionReservaResponse(reserva.getId(), reserva.getEstado(), tardia,
                penalizacion == null ? null : penalizacion.getFechaFinBloqueo(),
                tardia ? "Reserva cancelada con penalización: no podrá hacer nuevas reservas hasta la fecha indicada"
                        : "Reserva cancelada sin penalización");
    }

    // ------------------------------------------------------------------ consultas
    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> mias() {
        Afiliado afiliado = usuarioActual.afiliadoActual();
        return reservaRepository.findByAfiliadoIdOrderByFechaReservaDesc(afiliado.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> porActividad(Long actividadId) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
        Usuario actual = usuarioActual.obtener();
        if (actual.getRol() == Rol.ENTRENADOR && !actividad.getEntrenador().getId().equals(actual.getId())) {
            throw new AccessDeniedException("Solo puede consultar las actividades que tiene asignadas");
        }
        return reservaRepository.findByActividadIdAndEstadoIn(actividadId, CON_ASISTENCIA)
                .stream().map(this::toResponse).toList();
    }

    private ReservaResponse toResponse(Reserva r) {
        Pago pago = pagoRepository.findFirstByReservaIdAndEstado(r.getId(), EstadoPago.APROBADO).orElse(null);
        return toResponse(r, pago);
    }

    private ReservaResponse toResponse(Reserva r, Pago pago) {
        Actividad a = r.getActividad();
        return new ReservaResponse(
                r.getId(),
                r.getAfiliado().getId(),
                r.getAfiliado().getNombres() + " " + r.getAfiliado().getApellidos(),
                a.getId(),
                a.getDisciplina().getNombre(),
                a.getFecha(), a.getHoraInicio(), a.getHoraFin(),
                r.getEstado(), r.getOrigenCupo(), r.getFechaReserva(),
                r.getMembresia() == null ? null : r.getMembresia().getId(),
                pago == null ? null : PagoResponse.from(pago));
    }
}
