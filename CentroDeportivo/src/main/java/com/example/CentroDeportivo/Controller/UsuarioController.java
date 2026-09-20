package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.response.UsuarioResponse;
import com.example.CentroDeportivo.Service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(summary = "Consultar el perfil del usuario autenticado")
    public UsuarioResponse me(Authentication authentication) {
        return usuarioService.perfil(authentication.getName());
    }
}
