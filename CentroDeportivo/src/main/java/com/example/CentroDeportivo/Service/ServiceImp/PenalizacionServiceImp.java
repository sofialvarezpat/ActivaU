package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoPenalizacion;
import com.example.CentroDeportivo.Entity.Enum.TipoPenalizacion;
import com.example.CentroDeportivo.Entity.Penalizacion;
import com.example.CentroDeportivo.Entity.Reserva;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Repository.AfiliadoRepository;
import com.example.CentroDeportivo.Repository.PenalizacionRepository;
import com.example.CentroDeportivo.Repository.ReservaRepository;
import com.example.CentroDeportivo.Service.PenalizacionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PenalizacionServiceImp implements PenalizacionService {

    //  cancelaciones con menos de 12h de anticipación se penalizan
    private static final int HORAS_MINIMAS_SIN_PENALIZAR = 12;
    private static final double VALOR_PENALIZACION_CANCELACION_TARDIA = 10000.0;
    private static final double VALOR_PENALIZACION_INASISTENCIA = 20000.0;
    private static final int DIAS_BLOQUEO = 3; // 3 días de bloqueo tras cancelación tardía

    private final PenalizacionRepository penalizacionRepository;
    private final AfiliadoRepository afiliadoRepository;
    private final ReservaRepository reservaRepository;

    @Override
    @Transactional
    public List<Penalizacion> listarPorAfiliado(Long afiliadoId) {
        return penalizacionRepository.findByAfiliadoId(afiliadoId);
    }

    @Override
    @Transactional
    public Penalizacion aplicarPorCancelacionTardia(Long afiliadoId, Long reservaId,
                                                    int horasAnticipacion, String motivo) {
        // Si la anticipación es >= 12h, se cancela sin penalización
        if (horasAnticipacion >= HORAS_MINIMAS_SIN_PENALIZAR) {
            return null;
        }

        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Afiliado no encontrado: " + afiliadoId));
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada: " + reservaId));

        Penalizacion penalizacion = crearPenalizacion(
                afiliado, TipoPenalizacion.BLOQUEO_TEMPORAL, VALOR_PENALIZACION_CANCELACION_TARDIA, motivo);
        penalizacion.setReserva(reserva);
        penalizacion.setHorasAnticipacionCancelacion(horasAnticipacion);

        return penalizacionRepository.save(penalizacion);
    }

    @Override
    public Penalizacion aplicarPorInasistencia(Long afiliadoId, Long reservaId, String motivo) {
        return null;
    }

    @Transactional
    @Override
    public Penalizacion aplicarPorInasistencia(Long afiliadoId, String motivo) {
        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Afiliado no encontrado: " + afiliadoId));

        Penalizacion penalizacion = crearPenalizacion(
                afiliado, TipoPenalizacion.PERDIDA_CUPO, VALOR_PENALIZACION_INASISTENCIA, motivo);

        return penalizacionRepository.save(penalizacion);
    }

    @Override
    @Transactional
    public void liberarBloqueosVencidos() {
        List<Penalizacion> bloqueosVencidos = penalizacionRepository
                .findByFechaFinBloqueoLessThanEqual(LocalDateTime.now());

        bloqueosVencidos.forEach(p -> {
            Afiliado afiliado = p.getAfiliado();
            afiliado.setEstadoPenalizacion(EstadoPenalizacion.SIN_PENALIZACION);
            afiliadoRepository.save(afiliado);
        });
    }

    private Penalizacion crearPenalizacion(Afiliado afiliado, TipoPenalizacion tipo, double valor, String motivo) {
        afiliado.setEstadoPenalizacion(EstadoPenalizacion.PENALIZADO);
        afiliadoRepository.save(afiliado);

        Penalizacion penalizacion = new Penalizacion();
        penalizacion.setAfiliado(afiliado);
        penalizacion.setTipo(tipo);
        penalizacion.setValor(valor);
        penalizacion.setMotivo(motivo);
        penalizacion.setFecha(LocalDateTime.now());
        penalizacion.setFechaFinBloqueo(LocalDateTime.now().plusDays(DIAS_BLOQUEO));

        return penalizacion;
    }
}