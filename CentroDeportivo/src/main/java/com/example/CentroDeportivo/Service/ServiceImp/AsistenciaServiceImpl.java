package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.AsistenciaRequest;
import com.example.CentroDeportivo.DTO.response.AsistenciaResponse;
import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Asistencia;
import com.example.CentroDeportivo.Entity.Enum.EstadoAsistencia;
import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;
import com.example.CentroDeportivo.Entity.Enum.Rol;
import com.example.CentroDeportivo.Entity.Reserva;
import com.example.CentroDeportivo.Entity.Usuario;
import com.example.CentroDeportivo.Exception.BusinessRuleException;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.AsistenciaRepository;
import com.example.CentroDeportivo.Repository.ReservaRepository;
import com.example.CentroDeportivo.Service.AsistenciaService;
import com.example.CentroDeportivo.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AsistenciaServiceImpl implements AsistenciaService {

    private final ReservaRepository reservaRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioActual usuarioActual;
    private final Clock clock;

    @Override
    @Transactional
    public AsistenciaResponse registrar(AsistenciaRequest req) {
        Reserva reserva = reservaRepository.findById(req.reservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        Actividad actividad = reserva.getActividad();

        Usuario actual = usuarioActual.obtener();
        if (actual.getRol() == Rol.ENTRENADOR && !actividad.getEntrenador().getId().equals(actual.getId())) {
            throw new AccessDeniedException("Solo puede registrar asistencia de sus propias actividades");
        }
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ConflictException("Solo se puede registrar asistencia de reservas confirmadas");
        }
        LocalDateTime ahora = LocalDateTime.now(clock);
        if (LocalDateTime.of(actividad.getFecha(), actividad.getHoraInicio()).isAfter(ahora)) {
            throw new BusinessRuleException("No se puede registrar asistencia antes de que inicie la clase");
        }
        if (asistenciaRepository.existsByReservaId(reserva.getId())) {
            throw new ConflictException("La asistencia de esta reserva ya fue registrada");
        }
        if (req.estado() == EstadoAsistencia.AUSENTE_JUSTIFICADO
                && (req.observacion() == null || req.observacion().isBlank())) {
            throw new BusinessRuleException("Una inasistencia justificada requiere una observación");
        }

        Asistencia asistencia = new Asistencia();
        asistencia.setReserva(reserva);
        asistencia.setEstado(req.estado());
        asistencia.setHora(ahora);
        asistencia.setObservaciones(req.observacion());
        asistencia = asistenciaRepository.save(asistencia);

        reserva.setEstado(req.estado() == EstadoAsistencia.PRESENTE ? EstadoReserva.ASISTIDA : EstadoReserva.NO_ASISTIDA);
        reservaRepository.save(reserva);

        return new AsistenciaResponse(asistencia.getId(), reserva.getId(), asistencia.getEstado(),
                asistencia.getHora(), asistencia.getObservaciones(), reserva.getEstado());
    }
}
