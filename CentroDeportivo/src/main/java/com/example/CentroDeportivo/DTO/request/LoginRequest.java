package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String correo,
        @NotBlank String contrasena) {
}
