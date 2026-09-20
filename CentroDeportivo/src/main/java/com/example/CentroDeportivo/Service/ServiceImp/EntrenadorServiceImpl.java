package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.EntrenadorRequest;
import com.example.CentroDeportivo.DTO.request.EntrenadorUpdateRequest;
import com.example.CentroDeportivo.DTO.response.EntrenadorResponse;
import com.example.CentroDeportivo.Entity.Entrenador;
import com.example.CentroDeportivo.Entity.Enum.EstadoUsuario;
import com.example.CentroDeportivo.Entity.Enum.Rol;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.EntrenadorRepository;
import com.example.CentroDeportivo.Repository.UsuarioRepository;
import com.example.CentroDeportivo.Service.EntrenadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntrenadorServiceImpl implements EntrenadorService {

    private final EntrenadorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<EntrenadorResponse> listar() {
        return repository.findAll().stream().map(EntrenadorResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EntrenadorResponse obtener(Long id) {
        return EntrenadorResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public EntrenadorResponse crear(EntrenadorRequest req) {
        String correo = req.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ConflictException("El correo ya está registrado");
        }
        Entrenador e = new Entrenador();
        e.setCorreo(correo);
        e.setContrasena(passwordEncoder.encode(req.contrasena()));
        e.setRol(Rol.ENTRENADOR);
        e.setNombres(req.nombres());
        e.setApellidos(req.apellidos());
        e.setTelefono(req.telefono());
        e.setEspecialidad(req.especialidad());
        e.setDisponibilidad(req.disponibilidad());
        return EntrenadorResponse.from(repository.save(e));
    }

    @Override
    @Transactional
    public EntrenadorResponse actualizar(Long id, EntrenadorUpdateRequest req) {
        Entrenador e = buscar(id);
        e.setNombres(req.nombres());
        e.setApellidos(req.apellidos());
        e.setTelefono(req.telefono());
        e.setEspecialidad(req.especialidad());
        e.setDisponibilidad(req.disponibilidad());
        if (req.estado() != null) {
            e.setEstado(req.estado());
        }
        return EntrenadorResponse.from(repository.save(e));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Entrenador e = buscar(id);
        e.setEstado(EstadoUsuario.INACTIVO);
        repository.save(e);
    }

    private Entrenador buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado"));
    }
}
