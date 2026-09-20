package com.example.CentroDeportivo.DTO.response;

import java.time.LocalDate;

public record AsistenciaReporteResponse(
        LocalDate desde,
        LocalDate hasta,
        long presentes,
        long ausentes,
        long ausentesJustificados,
        long total,
        double porcentajeAsistencia) {
}
