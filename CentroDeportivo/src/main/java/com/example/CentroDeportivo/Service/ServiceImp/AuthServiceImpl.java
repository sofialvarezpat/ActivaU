package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.LoginRequest;
import com.example.CentroDeportivo.DTO.request.RegisterRequest;
import com.example.CentroDeportivo.DTO.response.AuthResponse;
import com.example.CentroDeportivo.DTO.response.UsuarioResponse;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Entrenador;
import com.example.CentroDeportivo.Entity.Enum.Rol;
import com.example.CentroDeportivo.Entity.Usuario;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Repository.UsuarioRepository;
import com.example.CentroDeportivo.Service.AuthService;
import com.example.CentroDeportivo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Registro público: solo AFILIADO. Cualquier otro rol exige que quien llama
     * sea un ADMINISTRADOR autenticado (evita que cualquiera se cree una cuenta admin).
     */
    @Override
    @Transactional
    public UsuarioResponse registrar(RegisterRequest req) {
        if (req.rol() != Rol.AFILIADO && !llamadorEsAdministrador()) {
            throw new AccessDeniedException("Solo un administrador puede crear cuentas con este rol");
        }
        String correo = req.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictException("El correo ya está registrado");
        }

        Usuario usuario = switch (req.rol()) {
            case AFILIADO -> new Afiliado();
            case ENTRENADOR -> new Entrenador();
            default -> new Usuario(); // RECEPCIONISTA, COORDINADOR, ADMINISTRADOR
        };
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(req.contrasena())); // nunca en texto plano
        usuario.setNombres(req.nombres());
        usuario.setApellidos(req.apellidos());
        usuario.setTelefono(req.telefono());
        usuario.setRol(req.rol());

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.correo().trim().toLowerCase(), req.contrasena()));
        UserDetails user = (UserDetails) auth.getPrincipal();
        String rol = user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return new AuthResponse(jwtService.generarToken(user), "Bearer", jwtService.getExpirationMs(), rol);
    }

    private boolean llamadorEsAdministrador() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.isAuthenticated()
                && a.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }
}
