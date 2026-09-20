package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.LoginRequest;
import com.example.CentroDeportivo.DTO.request.RegisterRequest;
import com.example.CentroDeportivo.DTO.response.AuthResponse;
import com.example.CentroDeportivo.DTO.response.UsuarioResponse;
import com.example.CentroDeportivo.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro e inicio de sesión (RF01)")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar usuario",
            description = "Público solo para rol AFILIADO. Otros roles requieren token de ADMINISTRADOR.")
    public UsuarioResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.registrar(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar y devolver token JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
