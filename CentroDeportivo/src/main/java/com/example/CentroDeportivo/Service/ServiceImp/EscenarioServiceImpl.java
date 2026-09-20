package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.EscenarioRequest;
import com.example.CentroDeportivo.DTO.response.EscenarioResponse;
import com.example.CentroDeportivo.Entity.Enum.EstadoEscenario;
import com.example.CentroDeportivo.Entity.Escenario;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.EscenarioRepository;
import com.example.CentroDeportivo.Service.EscenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EscenarioServiceImpl implements EscenarioService {

    private final EscenarioRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<EscenarioResponse> listar() {
        return repository.findAll().stream().map(EscenarioResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EscenarioResponse obtener(Long id) {
        return EscenarioResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public EscenarioResponse crear(EscenarioRequest req) {
        if (repository.existsByNombreIgnoreCase(req.nombre().trim())) {
            throw new ConflictException("Ya existe un escenario con ese nombre");
        }
        Escenario e = new Escenario();
        aplicar(e, req);
        return EscenarioResponse.from(repository.save(e));
    }

    @Override
    @Transactional
    public EscenarioResponse actualizar(Long id, EscenarioRequest req) {
        Escenario e = buscar(id);
        if (repository.existsByNombreIgnoreCaseAndIdNot(req.nombre().trim(), id)) {
            throw new ConflictException("Ya existe otro escenario con ese nombre");
        }
        aplicar(e, req);
        return EscenarioResponse.from(repository.save(e));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "No se puede eliminar el escenario porque tiene actividades asociadas; márquelo en MANTENIMIENTO");
        }
    }

    private Escenario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escenario no encontrado"));
    }

    private void aplicar(Escenario e, EscenarioRequest req) {
        e.setNombre(req.nombre().trim());
        e.setTipo(req.tipo());
        e.setUbicacion(req.ubicacion());
        e.setCapacidadMaxima(req.capacidadMaxima());
        e.setEstado(req.estado() != null ? req.estado() : EstadoEscenario.DISPONIBLE);
    }
}
