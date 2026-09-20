package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoEscenario;

public record UsoEscenarioResponse(
        Long escenarioId,
        String escenario,
        EstadoEscenario estado,
        long actividades,
        double horasUso) {
}
