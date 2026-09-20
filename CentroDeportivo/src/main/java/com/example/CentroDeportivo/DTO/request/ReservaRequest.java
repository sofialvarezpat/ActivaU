package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Reservar una actividad. Use UNA de dos opciones: membresiaId (membresía vigente) o datosPago.
 * Si la actividad es gratuita no se requiere ninguna.
 * afiliadoId solo lo indica el personal (recepción/administración); un afiliado reserva para sí mismo.
 */
public record ReservaRequest(
        @NotNull Long actividadId,
        Long afiliadoId,
        Long membresiaId,
        @Valid DatosPagoRequest datosPago) {
}
