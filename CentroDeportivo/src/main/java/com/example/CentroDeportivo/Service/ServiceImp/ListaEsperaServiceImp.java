package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.ListaEspera;
import com.example.CentroDeportivo.Service.ListaEsperaService;

import java.util.List;
import java.util.Optional;

public class ListaEsperaServiceImp  implements ListaEsperaService {
    @Override
    public List<ListaEspera> listarPorActividad(Long actividadId) {
        return List.of();
    }

    @Override
    public ListaEspera inscribir(Long afiliadoId, Long actividadId) {
        return null;
    }

    @Override
    public Optional<ListaEspera> invitarSiguiente(Long actividadId) {
        return Optional.empty();
    }

    @Override
    public ListaEspera confirmarInvitacion(Long listaEsperaId) {
        return null;
    }

    @Override
    public ListaEspera expirarInvitacion(Long listaEsperaId) {
        return null;
    }

    @Override
    public ListaEspera obtenerPorId(Long id) {
        return null;
    }
}
