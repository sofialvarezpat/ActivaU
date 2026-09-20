package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.TipoMembresiaRequest;
import com.example.CentroDeportivo.DTO.response.TipoMembresiaResponse;
import com.example.CentroDeportivo.Entity.TipoMembresia;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.TipoMembresiaRepository;
import com.example.CentroDeportivo.Service.TipoMembresiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoMembresiaServiceImpl implements TipoMembresiaService {

    private final TipoMembresiaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoMembresiaResponse> listar() {
        return repository.findAll().stream().map(TipoMembresiaResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoMembresiaResponse obtener(Long id) {
        return TipoMembresiaResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public TipoMembresiaResponse crear(TipoMembresiaRequest req) {
        if (repository.existsByNombreIgnoreCase(req.nombre().trim())) {
            throw new ConflictException("Ya existe un tipo de membresía con ese nombre");
        }
        TipoMembresia t = new TipoMembresia();
        aplicar(t, req);
        return TipoMembresiaResponse.from(repository.save(t));
    }

    @Override
    @Transactional
    public TipoMembresiaResponse actualizar(Long id, TipoMembresiaRequest req) {
        TipoMembresia t = buscar(id);
        if (repository.existsByNombreIgnoreCaseAndIdNot(req.nombre().trim(), id)) {
            throw new ConflictException("Ya existe otro tipo de membresía con ese nombre");
        }
        aplicar(t, req);
        return TipoMembresiaResponse.from(repository.save(t));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("No se puede eliminar el tipo de membresía porque tiene membresías asociadas");
        }
    }

    private TipoMembresia buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de membresía no encontrado"));
    }

    private void aplicar(TipoMembresia t, TipoMembresiaRequest req) {
        t.setNombre(req.nombre().trim());
        t.setPrecio(req.precio());
        t.setDuracionDias(req.duracionDias());
        t.setBeneficios(req.beneficios());
    }
}
