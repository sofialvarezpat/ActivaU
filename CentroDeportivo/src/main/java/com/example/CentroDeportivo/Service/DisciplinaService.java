package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.DisciplinaRequest;
import com.example.CentroDeportivo.DTO.response.DisciplinaResponse;

import java.util.List;

public interface DisciplinaService {

    List<DisciplinaResponse> listar();

    DisciplinaResponse obtener(Long id);

    DisciplinaResponse crear(DisciplinaRequest request);

    DisciplinaResponse actualizar(Long id, DisciplinaRequest request);

    void eliminar(Long id);
}
