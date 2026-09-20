package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoEscenario;
import com.example.CentroDeportivo.Entity.Escenario;

public record EscenarioResponse(Long id, String nombre, String tipo, String ubicacion,
                                Integer capacidadMaxima, EstadoEscenario estado) {

    public static EscenarioResponse from(Escenario e) {
        return new EscenarioResponse(e.getId(), e.getNombre(), e.getTipo(), e.getUbicacion(),
                e.getCapacidadMaxima(), e.getEstado());
    }
}
