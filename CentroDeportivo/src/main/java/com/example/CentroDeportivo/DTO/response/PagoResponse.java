package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoPago;
import com.example.CentroDeportivo.Entity.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        String concepto,
        BigDecimal valor,
        EstadoPago estado,
        String referenciaPasarela,
        String tarjetaEnmascarada,
        String mensajeBanco,
        LocalDateTime fechaPago) {

    public static PagoResponse from(Pago p) {
        return new PagoResponse(p.getId(), p.getConcepto(), p.getValor(), p.getEstado(),
                p.getReferenciaPasarela(), p.getTarjetaEnmascarada(), p.getMensajeBanco(), p.getFechaPago());
    }
}
