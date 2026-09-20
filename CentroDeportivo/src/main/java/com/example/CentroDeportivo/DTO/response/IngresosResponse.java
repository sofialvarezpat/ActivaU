package com.example.CentroDeportivo.DTO.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IngresosResponse(
        LocalDate desde,
        LocalDate hasta,
        BigDecimal ingresosReservas,
        BigDecimal ingresosMembresias,
        BigDecimal total,
        long pagosAprobados,
        long pagosRechazados) {
}
