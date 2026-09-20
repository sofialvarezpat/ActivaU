package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.Valid;

public record AceptarInvitacionRequest(
        Long membresiaId,
        @Valid DatosPagoRequest datosPago) {
}
