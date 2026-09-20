package com.example.CentroDeportivo.DTO.request;

import com.example.CentroDeportivo.Entity.Enum.NivelDisciplina;
import jakarta.validation.constraints.*;

public record EntrenadorRequest( @NotBlank @Email @Size(max = 150)
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

                                 @Size(max = 100)
                                 String especialidad,

                                 @Size(max = 200)
                                 String disponibilidad) {
}
