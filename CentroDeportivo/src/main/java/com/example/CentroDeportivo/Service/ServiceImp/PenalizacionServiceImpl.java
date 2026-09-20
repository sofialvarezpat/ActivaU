package com.example.CentroDeportivo.Service.ServiceImp;


import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoPenalizacion;
import com.example.CentroDeportivo.Entity.Enum.TipoPenalizacion;
import com.example.CentroDeportivo.Entity.Penalizacion;
import com.example.CentroDeportivo.Entity.Reserva;
import com.example.CentroDeportivo.Exception.BusinessRuleException;
import com.example.CentroDeportivo.Repository.PenalizacionRepository;
import com.example.CentroDeportivo.Service.PenalizacionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** Política de penalización de RF11 (parámetros configurables en application.properties). */
@Service
public class PenalizacionServiceImpl implements PenalizacionService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PenalizacionRepository penalizacionRepository;
    private final Clock clock;
    private final long horasLimite;
    private final long diasBloqueo;

    public PenalizacionServiceImpl(PenalizacionRepository penalizacionRepository,
                                   Clock clock,
                                   @Value("${app.penalizacion.horas-limite:12}") long horasLimite,
                                   @Value("${app.penalizacion.dias-bloqueo:7}") long diasBloqueo) {
        this.penalizacionRepository = penalizacionRepository;
        this.clock = clock;
        this.horasLimite = horasLimite;
        this.diasBloqueo = diasBloqueo;
    }

    @Override
    @Transactional
    public void verificarSinPenalizacion(Afiliado afiliado) {
        if (afiliado.getEstadoPenalizacion() != EstadoPenalizacion.PENALIZADO) {
            return;
        }
        LocalDateTime ahora = LocalDateTime.now(clock);
        Optional<Penalizacion> ultima = penalizacionRepository.findFirstByAfiliadoIdOrderByFechaDesc(afiliado.getId());
        if (ultima.isPresent() && ultima.get().getFechaFinBloqueo() != null
                && ahora.isBefore(ultima.get().getFechaFinBloqueo())) {
            throw new BusinessRuleException("Tiene una penalización activa hasta "
                    + ultima.get().getFechaFinBloqueo().format(FORMATO) + "; no puede hacer nuevas reservas");
        }
        afiliado.setEstadoPenalizacion(EstadoPenalizacion.SIN_PENALIZACION); // el bloqueo ya se cumplió
    }

    @Override
    public boolean esCancelacionTardia(LocalDateTime ahora, LocalDateTime inicioActividad) {
        return Duration.between(ahora, inicioActividad).compareTo(Duration.ofHours(horasLimite)) < 0;
    }

    @Override
    @Transactional
    public Penalizacion registrarCancelacionTardia(Afiliado afiliado, Reserva reserva, LocalDateTime ahora) {
        Penalizacion p = new Penalizacion();
        p.setAfiliado(afiliado);
        p.setReserva(reserva);
        p.setTipo(TipoPenalizacion.BLOQUEO_TEMPORAL);
        p.setMotivo("Cancelación con menos de " + horasLimite + " horas de anticipación");
        p.setFecha(ahora);
        p.setFechaFinBloqueo(ahora.plusDays(diasBloqueo));
        afiliado.setEstadoPenalizacion(EstadoPenalizacion.PENALIZADO);
        return penalizacionRepository.save(p);
    }
}
