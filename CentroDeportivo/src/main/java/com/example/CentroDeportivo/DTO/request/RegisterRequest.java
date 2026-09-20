package com.example.CentroDeportivo.DTO.request;

import com.example.CentroDeportivo.Entity.Enum.Rol;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 150)
        String correo,

        @NotBlank
        @Pattern(regexp = Patrones.CONTRASENA, message = Patrones.CONTRASENA_MSG)
        String contrasena,

        @NotBlank @Size(max = 100)
        String nombres,

        @NotBlank @Size(max = 100)
        String apellidos,

        @Size(max = 20)
        String telefono,

        @NotNull
        Rol rol) {
}
