package com.example.CentroDeportivo.DTO;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservaRequestDTO {

    @NotNull(message = "El ID de la actividad es obligatorio")
    private Long actividadId;

    @NotNull(message = "El ID del afiliado es obligatorio")
    private Long afiliadoId;
}