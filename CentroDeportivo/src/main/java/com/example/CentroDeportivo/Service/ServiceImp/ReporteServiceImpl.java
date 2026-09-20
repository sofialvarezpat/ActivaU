package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.response.AsistenciaReporteResponse;
import com.example.CentroDeportivo.DTO.response.IngresosResponse;
import com.example.CentroDeportivo.DTO.response.OcupacionActividadResponse;
import com.example.CentroDeportivo.DTO.response.UsoEscenarioResponse;
import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoAsistencia;
import com.example.CentroDeportivo.Entity.Enum.EstadoPago;
import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;
import com.example.CentroDeportivo.Exception.BusinessRuleException;
import com.example.CentroDeportivo.Repository.*;
import com.example.CentroDeportivo.Service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private static final List<EstadoReserva> OCUPAN_CUPO =
            List.of(EstadoReserva.CONFIRMADA, EstadoReserva.ASISTIDA, EstadoReserva.NO_ASISTIDA);

    private final ActividadRepository actividadRepository;
    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final EscenarioRepository escenarioRepository;

    @Override
    public List<OcupacionActividadResponse> ocupacion(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        Map<Long, Long> reservas = new HashMap<>();
        reservaRepository.contarPorActividad(desde, hasta, OCUPAN_CUPO)
                .forEach(c -> reservas.put(c.getActividadId(), c.getTotal()));

        return actividadRepository
                .findByFechaBetweenAndEstadoNotOrderByFechaAscHoraInicioAsc(desde, hasta, EstadoActividad.CANCELADA)
                .stream()
                .map(a -> {
                    long n = reservas.getOrDefault(a.getId(), 0L);
                    return new OcupacionActividadResponse(a.getId(), a.getDisciplina().getNombre(),
                            a.getEscenario().getNombre(), a.getFecha(), a.getHoraInicio(), a.getCupoMaximo(),
                            n, porcentaje(n, a.getCupoMaximo()));
                })
                .toList();
    }

    @Override
    public IngresosResponse ingresos(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay(); // rango [ini, fin)

        BigDecimal reservas = nvl(pagoRepository.sumaPorReservas(EstadoPago.APROBADO, ini, fin));
        BigDecimal membresias = nvl(pagoRepository.sumaPorMembresias(EstadoPago.APROBADO, ini, fin));
        return new IngresosResponse(desde, hasta, reservas, membresias, reservas.add(membresias),
                pagoRepository.contarPorEstado(EstadoPago.APROBADO, ini, fin),
                pagoRepository.contarPorEstado(EstadoPago.RECHAZADO, ini, fin));
    }

    @Override
    public AsistenciaReporteResponse asistencia(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        Map<EstadoAsistencia, Long> conteo = new EnumMap<>(EstadoAsistencia.class);
        asistenciaRepository.contarPorEstado(desde, hasta).forEach(c -> conteo.put(c.getEstado(), c.getTotal()));

        long presentes = conteo.getOrDefault(EstadoAsistencia.PRESENTE, 0L);
        long ausentes = conteo.getOrDefault(EstadoAsistencia.AUSENTE, 0L);
        long justificados = conteo.getOrDefault(EstadoAsistencia.AUSENTE_JUSTIFICADO, 0L);
        long total = presentes + ausentes + justificados;
        return new AsistenciaReporteResponse(desde, hasta, presentes, ausentes, justificados, total,
                porcentaje(presentes, total));
    }

    @Override
    public List<UsoEscenarioResponse> usoEscenarios(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        Map<Long, long[]> acumulado = new HashMap<>(); // [0]=actividades, [1]=minutos
        for (Actividad a : actividadRepository
                .findByFechaBetweenAndEstadoNotOrderByFechaAscHoraInicioAsc(desde, hasta, EstadoActividad.CANCELADA)) {
            long[] datos = acumulado.computeIfAbsent(a.getEscenario().getId(), k -> new long[2]);
            datos[0]++;
            datos[1] += Duration.between(a.getHoraInicio(), a.getHoraFin()).toMinutes();
        }
        return escenarioRepository.findAll().stream()
                .map(e -> {
                    long[] d = acumulado.getOrDefault(e.getId(), new long[2]);
                    return new UsoEscenarioResponse(e.getId(), e.getNombre(), e.getEstado(), d[0],
                            Math.round(d[1] / 60.0 * 100.0) / 100.0);
                })
                .toList();
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new BusinessRuleException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }

    private BigDecimal nvl(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private double porcentaje(long parte, long total) {
        return total <= 0 ? 0.0 : Math.round(parte * 10000.0 / total) / 100.0;
    }
}
