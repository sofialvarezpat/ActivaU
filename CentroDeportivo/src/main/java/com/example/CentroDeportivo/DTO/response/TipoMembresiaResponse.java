package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.TipoMembresia;

import java.math.BigDecimal;

public record TipoMembresiaResponse(Long id, String nombre, BigDecimal precio,
                                    Integer duracionDias, String beneficios) {

    public static TipoMembresiaResponse from(TipoMembresia t) {
        return new TipoMembresiaResponse(t.getId(), t.getNombre(), t.getPrecio(),
                t.getDuracionDias(), t.getBeneficios());
    }
}
