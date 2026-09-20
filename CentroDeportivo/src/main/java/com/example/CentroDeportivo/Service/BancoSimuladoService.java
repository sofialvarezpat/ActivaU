package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.BancoRequest;
import com.example.CentroDeportivo.DTO.response.RespuestaBanco;

/** Pasarela de pago simulada (RF12). Sustituible por un cliente HTTP real sin tocar el resto. */
public interface BancoSimuladoService {

    RespuestaBanco procesar(BancoRequest request);
}
