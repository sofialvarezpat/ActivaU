package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.TipoMembresia;
import com.example.CentroDeportivo.Service.TipoMembresiaService;

import java.util.List;

import com.example.CentroDeportivo.Entity.TipoMembresia;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Repository.TipoMembresiaRepository;
import com.example.CentroDeportivo.Service.TipoMembresiaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TipoMembresiaServiceImp implements TipoMembresiaService {

    private final TipoMembresiaRepository tipoMembresiaRepository;

    @Override
    @Transactional
    public List<TipoMembresia> listarTodos() {
        return tipoMembresiaRepository.findAll();
    }

    @Override
    @Transactional
    public TipoMembresia obtenerPorId(Long id) {
        return tipoMembresiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de membresía no encontrado: " + id));
    }

    @Override
    @Transactional
    public TipoMembresia crear(TipoMembresia tipoMembresia) {
        return tipoMembresiaRepository.save(tipoMembresia);
    }

    @Override
    @Transactional
    public TipoMembresia actualizar(Long id, TipoMembresia cambios) {
        TipoMembresia existente = obtenerPorId(id);
        existente.setNombre(cambios.getNombre());
        existente.setPrecio(cambios.getPrecio());
        existente.setDuracionDias(cambios.getDuracionDias());
        existente.setBeneficios(cambios.getBeneficios());
        return tipoMembresiaRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        TipoMembresia existente = obtenerPorId(id);
        tipoMembresiaRepository.delete(existente);
    }
}