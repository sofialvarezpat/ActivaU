package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.response.UsuarioResponse;

public interface UsuarioService {

    UsuarioResponse perfil(String correo);
}
