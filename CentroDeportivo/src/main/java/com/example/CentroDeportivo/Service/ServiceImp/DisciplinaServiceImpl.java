package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.DisciplinaRequest;
import com.example.CentroDeportivo.DTO.response.DisciplinaResponse;
import com.example.CentroDeportivo.Entity.Disciplina;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.DisciplinaRepository;
import com.example.CentroDeportivo.Service.DisciplinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaServiceImpl implements DisciplinaService {

    private final DisciplinaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<DisciplinaResponse> listar() {
        return repository.findAll().stream().map(DisciplinaResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DisciplinaResponse obtener(Long id) {
        return DisciplinaResponse.from(buscar(id));
    }

    @Override
    @Transactional
    public DisciplinaResponse crear(DisciplinaRequest req) {
        String nombre = req.nombre().trim();
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictException("Ya existe una disciplina con ese nombre");
        }
        Disciplina d = new Disciplina();
        aplicar(d, req);
        return DisciplinaResponse.from(repository.save(d));
    }

    @Override
    @Transactional
    public DisciplinaResponse actualizar(Long id, DisciplinaRequest req) {
        Disciplina d = buscar(id);
        if (repository.existsByNombreIgnoreCaseAndIdNot(req.nombre().trim(), id)) {
            throw new ConflictException("Ya existe otra disciplina con ese nombre");
        }
        aplicar(d, req);
        return DisciplinaResponse.from(repository.save(d));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("No se puede eliminar la disciplina porque tiene actividades asociadas");
        }
    }

    private Disciplina buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina no encontrada"));
    }

    private void aplicar(Disciplina d, DisciplinaRequest req) {
        d.setNombre(req.nombre().trim());
        d.setDescripcion(req.descripcion());
        d.setNivel(req.nivel());
    }
}
