package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.ActividadRequest;
import com.example.CentroDeportivo.DTO.request.CancelarActividadRequest;
import com.example.CentroDeportivo.DTO.request.FiltroActividades;
import com.example.CentroDeportivo.DTO.request.ReprogramarRequest;
import com.example.CentroDeportivo.DTO.response.ActividadResponse;
import com.example.CentroDeportivo.DTO.response.CupoResponse;
import com.example.CentroDeportivo.DTO.response.PageResponse;

public interface ActividadService {

    ActividadResponse programar(ActividadRequest request);

    PageResponse<ActividadResponse> listar(FiltroActividades filtro, int page, int size);

    ActividadResponse obtener(Long id);

    ActividadResponse reprogramar(Long id, ReprogramarRequest request);

    ActividadResponse cancelar(Long id, CancelarActividadRequest request);

    CupoResponse cupo(Long id);
}
