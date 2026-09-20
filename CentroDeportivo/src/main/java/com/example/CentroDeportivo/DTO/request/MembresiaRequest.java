package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record MembresiaRequest(
        @NotNull Long tipoMembresiaId,
        Long afiliadoId,
        @NotNull @Valid DatosPagoRequest datosPago) {
}
