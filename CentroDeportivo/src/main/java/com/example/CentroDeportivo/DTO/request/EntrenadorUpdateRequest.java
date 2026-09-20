package com.example.CentroDeportivo.DTO.request;

import com.example.CentroDeportivo.Entity.Enum.EstadoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntrenadorUpdateRequest(@NotBlank @Size(max = 100)
                                      String nombres,

                                      @NotBlank @Size(max = 100)
                                      String apellidos,

                                      @Size(max = 20)
                                      String telefono,

                                      @Size(max = 100)
                                      String especialidad,

                                      @Size(max = 200)
                                      String disponibilidad,

                                      /** Opcional: permite reactivar (ACTIVO) o desactivar (INACTIVO). */
                                      EstadoUsuario estado) {
}
