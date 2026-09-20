package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.DTO.response.AsistenciaReporteResponse;
import com.example.CentroDeportivo.DTO.response.IngresosResponse;
import com.example.CentroDeportivo.DTO.response.OcupacionActividadResponse;
import com.example.CentroDeportivo.DTO.response.UsoEscenarioResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {

    List<OcupacionActividadResponse> ocupacion(LocalDate desde, LocalDate hasta);

    IngresosResponse ingresos(LocalDate desde, LocalDate hasta);

    AsistenciaReporteResponse asistencia(LocalDate desde, LocalDate hasta);

    List<UsoEscenarioResponse> usoEscenarios(LocalDate desde, LocalDate hasta);
}
