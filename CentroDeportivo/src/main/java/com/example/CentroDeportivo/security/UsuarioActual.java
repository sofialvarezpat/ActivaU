package com.example.CentroDeportivo.security;


import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoUsuario;
import com.example.CentroDeportivo.Entity.Enum.Rol;
import com.example.CentroDeportivo.Entity.Usuario;
import com.example.CentroDeportivo.Exception.BusinessRuleException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.AfiliadoRepository;
import com.example.CentroDeportivo.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Acceso al usuario autenticado en la petición actual. */
@Component
@RequiredArgsConstructor
public class UsuarioActual {

    private final UsuarioRepository usuarioRepository;
    private final AfiliadoRepository afiliadoRepository;

    public Usuario obtener() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || a instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Sesión no válida");
        }
        return usuarioRepository.findByCorreo(a.getName())
                .orElseThrow(() -> new AccessDeniedException("Sesión no válida"));
    }

    public boolean tieneRol(Rol rol) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_" + rol.name()));
    }

    /** El afiliado que hace la petición (solo válido si el rol es AFILIADO). */
    public Afiliado afiliadoActual() {
        Usuario u = obtener();
        if (u.getRol() != Rol.AFILIADO) {
            throw new AccessDeniedException("Esta operación es solo para afiliados");
        }
        return afiliadoRepository.findById(u.getId())
                .orElseThrow(() -> new AccessDeniedException("Sesión no válida"));
    }

    /**
     * Un AFILIADO solo puede operar sobre sí mismo (si manda otro id: 403).
     * El personal (RECEPCIONISTA, ADMINISTRADOR) debe indicar el afiliado.
     */
    public Afiliado resolverAfiliado(Long afiliadoId) {
        Usuario u = obtener();
        if (u.getRol() == Rol.AFILIADO) {
            if (afiliadoId != null && !afiliadoId.equals(u.getId())) {
                throw new AccessDeniedException("Un afiliado solo puede operar sobre su propia cuenta");
            }
            return afiliadoActual();
        }
        if (afiliadoId == null) {
            throw new BusinessRuleException("Debe indicar el afiliado (afiliadoId)");
        }
        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Afiliado no encontrado"));
        if (afiliado.getEstado() != EstadoUsuario.ACTIVO) {
            throw new BusinessRuleException("El afiliado está inactivo");
        }
        return afiliado;
    }
}
