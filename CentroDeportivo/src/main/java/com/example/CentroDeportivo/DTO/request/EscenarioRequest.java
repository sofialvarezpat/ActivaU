package com.example.CentroDeportivo.DTO.request;

import com.example.CentroDeportivo.Entity.Enum.EstadoEscenario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EscenarioRequest(
        @NotBlank @Size(max = 100)
        String nombre,

        @Size(max = 50)
        String tipo,

        @Size(max = 150)
        String ubicacion,

        @NotNull @Min(1)
        Integer capacidadMaxima,

        /** Opcional: si viene nulo se asume DISPONIBLE. */
        EstadoEscenario estado
) {
}
