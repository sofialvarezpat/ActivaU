package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Reserva;
import com.example.CentroDeportivo.Service.ReservaService;

import java.util.List;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Reserva;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservaServiceImp implements ReservaService {

    // Estados según diagrama de actividad de Reserva
    private static final String ESTADO_EN_ESPERA = "EN_ESPERA";
    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    private static final String ESTADO_CANCELADA = "CANCELADA";

    private static final String ORIGEN_DIRECTO = "DIRECTO"; // placeholder, confirmar valores reales

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

        validarSinTraslapeConOtrasReservas(afiliadoId, actividad, null);

        Reserva reserva = new Reserva();
        reserva.setAfiliado(afiliado);
        reserva.setActividad(actividad);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setOrigenCupo(ORIGEN_DIRECTO);

        boolean hayCupo = actividad.getCuposDisponibles() != null && actividad.getCuposDisponibles() > 0;

        if (!hayCupo) {
            reserva.setEstado(ESTADO_EN_ESPERA);
            return reservaRepository.save(reserva);
        }

        // Hay cupo: según el diagrama, si el afiliado tiene membresía vigente,
        // se confirma de inmediato; si no, queda pendiente de pago.
        boolean tieneMembresiaVigente = membresiaService.tieneMembresiaVigente(afiliadoId);

        actividad.setCuposDisponibles(actividad.getCuposDisponibles() - 1);
        actividadRepository.save(actividad);

        if (tieneMembresiaVigente) {
            reserva.setEstado(ESTADO_CONFIRMADA);
        } else {
            // Pendiente de pago: se mantiene sin estado "CONFIRMADA" hasta que
            // PagoService llame a confirmarPorPago(...)
            reserva.setEstado(ESTADO_EN_ESPERA); // placeholder: revisar si debe ser otro estado, ej. "PENDIENTE_PAGO"
        }

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva confirmarPorPago(Long reservaId) {
        Reserva reserva = obtenerPorId(reservaId);
        reserva.setEstado(ESTADO_CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva cancelar(Long reservaId, String motivo) {
        Reserva reserva = obtenerPorId(reservaId);
        Actividad actividad = reserva.getActividad();

        reserva.setEstado(ESTADO_CANCELADA);
        reservaRepository.save(reserva);

        // Libera el cupo
        if (actividad.getCuposDisponibles() != null) {
            actividad.setCuposDisponibles(actividad.getCuposDisponibles() + 1);
            actividadRepository.save(actividad);
        }

        // Penaliza si la cancelación fue fuera de plazo (< 12h de anticipación, según diagrama de Penalización)
        LocalDateTime inicioActividad = LocalDateTime.of(actividad.getFecha(), actividad.getHoraInicio());
        long horasAnticipacion = Duration.between(LocalDateTime.now(), inicioActividad).toHours();

        if (horasAnticipacion < 12) {
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
                .findByAfiliadoIdAndEstadoNot(afiliadoId, ESTADO_CANCELADA);

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