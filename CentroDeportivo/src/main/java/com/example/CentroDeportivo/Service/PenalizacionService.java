package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Penalizacion;

import java.util.List;

public interface PenalizacionService {

    //Busqueda normal
    List<Penalizacion> listarPorAfiliado(Long afiliadoId);

    Penalizacion aplicarPorCancelacionTardia(Long afiliadoId, Long reservaId,
                                             int horasAnticipacion, String motivo);

    Penalizacion aplicarPorInasistencia(Long afiliadoId, String motivo);

    //Libera el bloqueo de los afiliados cuya penalización ya cumplio el periodo de bloqueo
    void liberarBloqueosVencidos();
}