package com.example.CentroDeportivo.DTO.request;

import com.example.CentroDeportivo.Entity.Enum.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AsistenciaRequest(
        @NotNull Long reservaId,
        @NotNull EstadoAsistencia estado,
        @Size(max = 300) String observacion) {
}
