package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Entrenador;
import com.example.CentroDeportivo.Entity.Enum.EstadoUsuario;

public record EntrenadorResponse(Long id, String correo, String nombres, String apellidos,
                                 String telefono, String especialidad, String disponibilidad,
                                 EstadoUsuario estado) {

    public static EntrenadorResponse from(Entrenador e) {
        return new EntrenadorResponse(e.getId(), e.getCorreo(), e.getNombres(), e.getApellidos(),
                e.getTelefono(), e.getEspecialidad(), e.getDisponibilidad(), e.getEstado());
    }
}
