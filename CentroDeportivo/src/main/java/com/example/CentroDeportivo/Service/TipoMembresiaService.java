package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.DTO.request.TipoMembresiaRequest;
import com.example.CentroDeportivo.DTO.response.TipoMembresiaResponse;

import java.util.List;

public interface TipoMembresiaService {

    List<TipoMembresiaResponse> listar();

    TipoMembresiaResponse obtener(Long id);

    TipoMembresiaResponse crear(TipoMembresiaRequest request);

    TipoMembresiaResponse actualizar(Long id, TipoMembresiaRequest request);

    void eliminar(Long id);
}
