package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoAsistencia;
import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;

import java.time.LocalDateTime;

public record AsistenciaResponse(
        Long id,
        Long reservaId,
        EstadoAsistencia estado,
        LocalDateTime hora,
        String observaciones,
        EstadoReserva estadoReserva) {
}
