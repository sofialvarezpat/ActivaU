package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoUsuario;
import com.example.CentroDeportivo.Entity.Enum.Rol;
import com.example.CentroDeportivo.Entity.Usuario;

public record UsuarioResponse(
        Long id,
        String correo,
        String nombres,
        String apellidos,
        String telefono,
        Rol rol,
        EstadoUsuario estado) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getCorreo(), u.getNombres(), u.getApellidos(),
                u.getTelefono(), u.getRol(), u.getEstado());
    }
}
