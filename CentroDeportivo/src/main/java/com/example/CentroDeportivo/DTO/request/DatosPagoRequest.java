package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record DatosPagoRequest(
        @NotBlank String numeroTarjeta,
        @NotBlank String fechaExpiracion,
        @NotBlank String cvv) {
}
