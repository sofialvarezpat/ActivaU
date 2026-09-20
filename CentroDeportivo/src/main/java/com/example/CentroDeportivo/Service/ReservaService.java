package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.AceptarInvitacionRequest;
import com.example.CentroDeportivo.DTO.request.ReservaRequest;
import com.example.CentroDeportivo.DTO.response.CancelacionReservaResponse;
import com.example.CentroDeportivo.DTO.response.ReservaResponse;

import java.util.List;

public interface ReservaService {

    ReservaResponse reservar(ReservaRequest request);

    ReservaResponse aceptarInvitacion(Long listaEsperaId, AceptarInvitacionRequest request);

    CancelacionReservaResponse cancelar(Long reservaId);

    List<ReservaResponse> mias();

    List<ReservaResponse> porActividad(Long actividadId);
}
