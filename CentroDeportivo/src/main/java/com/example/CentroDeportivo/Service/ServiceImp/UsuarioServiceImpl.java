package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.response.UsuarioResponse;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.UsuarioRepository;
import com.example.CentroDeportivo.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse perfil(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .map(UsuarioResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
