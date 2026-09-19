package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;
import com.example.CentroDeportivo.Entity.Enum.OrigenCupo;
import com.example.CentroDeportivo.Entity.ListaEspera;
import com.example.CentroDeportivo.Entity.Reserva;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Exception.ReglaNegocioException;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Repository.AfiliadoRepository;
import com.example.CentroDeportivo.Repository.ReservaRepository;
import com.example.CentroDeportivo.Service.ListaEsperaService;
import com.example.CentroDeportivo.Service.MembresiaService;
import com.example.CentroDeportivo.Service.PenalizacionService;
import com.example.CentroDeportivo.Service.ReservaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservaServiceImp implements ReservaService {

    // cancelar con menos de 12h de anticipación penaliza.
    private static final int HORAS_MINIMAS_SIN_PENALIZAR = 12;

    private final ReservaRepository reservaRepository;
    private final AfiliadoRepository afiliadoRepository;
    private final ActividadRepository actividadRepository;
    private final MembresiaService membresiaService;
    private final PenalizacionService penalizacionService;
    private final ListaEsperaService listaEsperaService;

    @Override
    @Transactional
    public List<Reserva> listarPorAfiliado(Long afiliadoId) {
        return reservaRepository.findByAfiliadoId(afiliadoId);
    }

    @Override
    @Transactional
    public Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada: " + id));
    }

    @Override
    @Transactional
    public Reserva reservar(Long afiliadoId, Long actividadId) {
        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Afiliado no encontrado: " + afiliadoId));
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada: " + actividadId));

        if (reservaRepository.existsByAfiliadoIdAndActividadId(afiliadoId, actividadId)) {
            throw new ConflictException("El afiliado ya tiene una reserva para esta actividad");
        }

        validarSinTraslapeConOtrasReservas(afiliadoId, actividad, null);

        boolean hayCupo = actividad.getCuposDisponibles() != null && actividad.getCuposDisponibles() > 0;

        if (!hayCupo) {
            // Sin cupo: NO se crea Reserva todavía. Se inscribe en la lista de espera
            // y la Reserva real se genera después, cuando se libera un cupo y el
            // afiliado confirma la invitación
            ListaEspera inscripcion = listaEsperaService.inscribir(afiliadoId, actividadId);
            throw new ReglaNegocioException(
                    "La actividad no tiene cupos disponibles. Se inscribió al afiliado en la lista de espera, posición "
                            + inscripcion.getPosicion());
        }

        Reserva reserva = new Reserva();
        reserva.setAfiliado(afiliado);
        reserva.setActividad(actividad);
        reserva.setOrigenCupo(OrigenCupo.DIRECTO);

        actividad.setCuposDisponibles(actividad.getCuposDisponibles() - 1);
        actividadRepository.save(actividad);

        boolean tieneMembresiaVigente = membresiaService.tieneMembresiaVigente(afiliadoId);
        reserva.setEstado(tieneMembresiaVigente ? EstadoReserva.CONFIRMADA : EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva confirmarDesdeListaEspera(Long listaEsperaId) {
        // confirmarInvitacion valida que la invitación esté en estado INVITADO
        // y la marca como ACEPTADO
        ListaEspera invitacion = listaEsperaService.confirmarInvitacion(listaEsperaId);

        Afiliado afiliado = invitacion.getAfiliado();
        Actividad actividad = invitacion.getActividad();

        if (reservaRepository.existsByAfiliadoIdAndActividadId(afiliado.getId(), actividad.getId())) {
            throw new ConflictException("El afiliado ya tiene una reserva para esta actividad");
        }

        Reserva reserva = new Reserva();
        reserva.setAfiliado(afiliado);
        reserva.setActividad(actividad);
        reserva.setOrigenCupo(OrigenCupo.LISTA_ESPERA);

        actividad.setCuposDisponibles(actividad.getCuposDisponibles() - 1);
        actividadRepository.save(actividad);

        boolean tieneMembresiaVigente = membresiaService.tieneMembresiaVigente(afiliado.getId());
        reserva.setEstado(tieneMembresiaVigente ? EstadoReserva.CONFIRMADA : EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva confirmarPorPago(Long reservaId) {
        Reserva reserva = obtenerPorId(reservaId);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ConflictException("No se puede confirmar una reserva que ya fue cancelada");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva cancelar(Long reservaId, String motivo) {
        Reserva reserva = obtenerPorId(reservaId);
        Actividad actividad = reserva.getActividad();

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ConflictException("La reserva ya estaba cancelada");
        }

        LocalDateTime inicioActividad = LocalDateTime.of(actividad.getFecha(), actividad.getHoraInicio());
        if (inicioActividad.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("No se puede cancelar una actividad que ya ocurrió");
        }

        long horasAnticipacion = Duration.between(LocalDateTime.now(), inicioActividad).toHours();

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // Libera el cupo
        if (actividad.getCuposDisponibles() != null) {
            actividad.setCuposDisponibles(actividad.getCuposDisponibles() + 1);
            actividadRepository.save(actividad);
        }

        // Penaliza si la cancelación fue fuera de plazo (< 12h de anticipación)
        if (horasAnticipacion < HORAS_MINIMAS_SIN_PENALIZAR) {
            penalizacionService.aplicarPorCancelacionTardia(
                    reserva.getAfiliado().getId(), reservaId, (int) horasAnticipacion, motivo);
        }

        // Invita al siguiente en la lista de espera de esa actividad
        listaEsperaService.invitarSiguiente(actividad.getId());

        return reserva;
    }

    @Override
    @Transactional
    public void validarSinTraslapeConOtrasReservas(Long afiliadoId, Actividad actividadNueva, Long reservaIdExcluir) {
        List<Reserva> reservasActivas = reservaRepository
                .findByAfiliadoIdAndEstadoNot(afiliadoId, EstadoReserva.CANCELADA);

        for (Reserva r : reservasActivas) {
            if (reservaIdExcluir != null && r.getId().equals(reservaIdExcluir)) {
                continue;
            }

            Actividad actividadExistente = r.getActividad();
            boolean mismoDia = actividadExistente.getFecha().equals(actividadNueva.getFecha());
            if (!mismoDia) {
                continue;
            }

            boolean seTraslapan = actividadNueva.getHoraInicio().isBefore(actividadExistente.getHoraFin())
                    && actividadExistente.getHoraInicio().isBefore(actividadNueva.getHoraFin());

            if (seTraslapan) {
                throw new ReglaNegocioException(
                        "El afiliado ya tiene una reserva que se traslapa con el horario de esta actividad");
            }
        }
    }
}