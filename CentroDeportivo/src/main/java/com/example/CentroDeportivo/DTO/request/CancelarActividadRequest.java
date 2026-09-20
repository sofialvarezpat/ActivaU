package com.example.CentroDeportivo.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarActividadRequest(
        @NotBlank @Size(max = 300) String motivo) {
}
