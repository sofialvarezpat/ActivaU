package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;

import java.time.LocalDateTime;

public record CancelacionReservaResponse(
        Long reservaId,
        EstadoReserva estado,
        boolean penalizado,
        LocalDateTime bloqueadoHasta,
        String mensaje) {
}
