package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.response.AsistenciaReporteResponse;
import com.example.CentroDeportivo.DTO.response.IngresosResponse;
import com.example.CentroDeportivo.DTO.response.OcupacionActividadResponse;
import com.example.CentroDeportivo.DTO.response.UsoEscenarioResponse;
import com.example.CentroDeportivo.Service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Reportes", description = "Indicadores (RF09). Uso exclusivo del ADMINISTRADOR. Fechas: yyyy-MM-dd")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ocupacion")
    @Operation(summary = "Ocupación de actividades")
    public List<OcupacionActividadResponse> ocupacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.ocupacion(desde, hasta);
    }

    @GetMapping("/ingresos")
    @Operation(summary = "Ingresos por reservas y membresías")
    public IngresosResponse ingresos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.ingresos(desde, hasta);
    }

    @GetMapping("/asistencia")
    @Operation(summary = "Asistencia e inasistencias")
    public AsistenciaReporteResponse asistencia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.asistencia(desde, hasta);
    }

    @GetMapping("/uso-escenarios")
    @Operation(summary = "Uso de escenarios (actividades y horas)")
    public List<UsoEscenarioResponse> usoEscenarios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.usoEscenarios(desde, hasta);
    }
}
