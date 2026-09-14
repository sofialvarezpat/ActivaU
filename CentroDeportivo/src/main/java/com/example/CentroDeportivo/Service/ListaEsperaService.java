package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.ListaEspera;

import java.util.List;
import java.util.Optional;

public interface ListaEsperaService {

    //Busqueda normal
    List<ListaEspera> listarPorActividad(Long actividadId);

    ListaEspera inscribir(Long afiliadoId, Long actividadId);

    //Se invoca cuando se libera un cupo por cancelacion de reserva
    //Invita trazablemente al primer afiliado en espera
    Optional<ListaEspera> invitarSiguiente(Long actividadId);

    ListaEspera confirmarInvitacion(Long listaEsperaId);

    ListaEspera expirarInvitacion(Long listaEsperaId);

    ListaEspera obtenerPorId(Long id);
}