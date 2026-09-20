package com.example.CentroDeportivo.DTO.request;

import com.example.CentroDeportivo.Entity.Enum.NivelDisciplina;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DisciplinaRequest(@NotBlank @Size(max = 100)
                                String nombre,

                                @Size(max = 500)
                                String descripcion,

                                @NotNull
                                NivelDisciplina nivel) {
}
