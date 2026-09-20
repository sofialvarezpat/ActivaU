package com.example.CentroDeportivo.Config;

import com.example.CentroDeportivo.Entity.Enum.Rol;
import com.example.CentroDeportivo.Entity.Usuario;
import com.example.CentroDeportivo.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Crea el primer ADMINISTRADOR (los demás roles internos los crea él vía /api/auth/register). */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByCorreo(adminEmail)) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNombres("Administrador");
        admin.setApellidos("ActivaU");
        admin.setCorreo(adminEmail);
        admin.setContrasena(passwordEncoder.encode(adminPassword));
        admin.setRol(Rol.ADMINISTRADOR);
        usuarioRepository.save(admin);
        log.info("Usuario administrador inicial creado: {}", adminEmail);
    }
}
