package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Penalizacion;

import java.util.List;

public interface PenalizacionService {

    //Busqueda normal
    List<Penalizacion> listarPorAfiliado(Long afiliadoId);

    Penalizacion aplicarPorCancelacionTardia(Long afiliadoId, Long reservaId,
                                             int horasAnticipacion, String motivo);

    //OCORRECCION: se agrega el parámetro reservaId porque la entidad Penalizacion tiene una
    //relación ManyToOne hacia Reserva
    //Sin esto no habria forma de persistir a qué
    //reserva/clase corresponde la inasistencia que genero la penalización
    Penalizacion aplicarPorInasistencia(Long afiliadoId, Long reservaId, String motivo);

    //Libera el bloqueo de los afiliados cuya penalización ya cumplio el periodo de bloqueo
    void liberarBloqueosVencidos();
}