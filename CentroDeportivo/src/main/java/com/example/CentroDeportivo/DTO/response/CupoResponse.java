package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoListaEspera;

/** Estado de cupos de una actividad y, si quien consulta es afiliado, su lugar en la lista de espera. */
public record CupoResponse(
        Long actividadId,
        Integer cupoMaximo,
        Integer cuposDisponibles,
        long personasEnEspera,
        Integer miPosicion,
        EstadoListaEspera miEstadoEnEspera) {
}
