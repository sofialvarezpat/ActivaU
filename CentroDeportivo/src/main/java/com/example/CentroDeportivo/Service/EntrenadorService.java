package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.DTO.request.EntrenadorRequest;
import com.example.CentroDeportivo.DTO.request.EntrenadorUpdateRequest;
import com.example.CentroDeportivo.DTO.response.EntrenadorResponse;

import java.util.List;

public interface EntrenadorService {

    List<EntrenadorResponse> listar();

    EntrenadorResponse obtener(Long id);

    EntrenadorResponse crear(EntrenadorRequest request);

    EntrenadorResponse actualizar(Long id, EntrenadorUpdateRequest request);

    /** Desactiva al entrenador (no se borra: conserva el historial de actividades). */
    void desactivar(Long id);
}
