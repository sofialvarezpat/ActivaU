package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.EscenarioRequest;
import com.example.CentroDeportivo.DTO.response.EscenarioResponse;

import java.util.List;

public interface EscenarioService {

    List<EscenarioResponse> listar();

    EscenarioResponse obtener(Long id);

    EscenarioResponse crear(EscenarioRequest request);

    EscenarioResponse actualizar(Long id, EscenarioRequest request);

    void eliminar(Long id);
}
