package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BancoRequest(
        @NotBlank String numeroTarjeta,
        @NotBlank String fechaExpiracion,
        @NotBlank String cvv,
        @NotNull @DecimalMin(value = "0.01") BigDecimal monto,
        Long reservaId,
        Long afiliadoId) {
}
