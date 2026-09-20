package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.AsistenciaRequest;
import com.example.CentroDeportivo.DTO.response.AsistenciaResponse;

public interface AsistenciaService {

    AsistenciaResponse registrar(AsistenciaRequest request);
}
