package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Asistencia;

import java.util.List;

public interface AsistenciaService {

    //Busqueda normal
    List<Asistencia> listarTodas();

    Asistencia obtenerPorReserva(Long reservaId);

    Asistencia registrarPresente(Long reservaId);

    //Boolean porque la inasistencia puede ser justificada o no
    Asistencia registrarInasistencia(Long reservaId, String observacion, boolean justificada);

    Asistencia actualizarObservacion(Long asistenciaId, String observacion);
}