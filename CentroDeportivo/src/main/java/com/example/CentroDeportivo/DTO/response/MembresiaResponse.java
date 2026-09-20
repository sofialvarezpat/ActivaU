package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoMembresia;
import com.example.CentroDeportivo.Entity.Membresia;

import java.time.LocalDate;

public record MembresiaResponse(
        Long id,
        Long afiliadoId,
        Long tipoMembresiaId,
        String tipo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoMembresia estado,
        PagoResponse pago) {

    /** El estado se calcula: una membresía ACTIVA cuya fecha de fin ya pasó se muestra como VENCIDA. */
    public static MembresiaResponse from(Membresia m, LocalDate hoy, PagoResponse pago) {
        EstadoMembresia estado = (m.getEstado() == EstadoMembresia.ACTIVA && hoy.isAfter(m.getFechaFin()))
                ? EstadoMembresia.VENCIDA : m.getEstado();
        return new MembresiaResponse(m.getId(), m.getAfiliado().getId(), m.getTipoMembresia().getId(),
                m.getTipoMembresia().getNombre(), m.getFechaInicio(), m.getFechaFin(), estado, pago);
    }
}
