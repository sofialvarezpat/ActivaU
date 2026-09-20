package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.DatosPagoRequest;
import com.example.CentroDeportivo.DTO.response.PageResponse;
import com.example.CentroDeportivo.DTO.response.PagoResponse;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Pago;

import java.math.BigDecimal;
import java.util.List;

public interface PagoService {

    /**
     * Cobra a través de la pasarela. Si la aprueban devuelve el Pago APROBADO (aún sin reserva/membresía);
     * si la rechazan guarda un Pago RECHAZADO y lanza PagoRechazadoException.
     */
    Pago cobrar(Afiliado afiliado, BigDecimal valor, String concepto, DatosPagoRequest datos);

    List<PagoResponse> mios();

    PageResponse<PagoResponse> listar(int page, int size);
}
