package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Disciplina;
import com.example.CentroDeportivo.Entity.Enum.NivelDisciplina;

public record DisciplinaResponse(Long id, String nombre, String descripcion, NivelDisciplina nivel) {

    public static DisciplinaResponse from(Disciplina d) {
        return new DisciplinaResponse(d.getId(), d.getNombre(), d.getDescripcion(), d.getNivel());
    }
}
