package com.example.CentroDeportivo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioRegistroDTO {
    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contraseña;

    private String telefono;

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String documentoIdentidad;

    private LocalDate fechaNacimiento;
}