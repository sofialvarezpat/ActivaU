package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.LoginRequest;
import com.example.CentroDeportivo.DTO.request.RegisterRequest;
import com.example.CentroDeportivo.DTO.response.AuthResponse;
import com.example.CentroDeportivo.DTO.response.UsuarioResponse;

public interface AuthService {

    UsuarioResponse registrar(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
