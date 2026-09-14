package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Pago;

import java.util.List;

public interface PagoService {

    //Busqueda normal
    List<Pago> listarPorAfiliado(Long afiliadoId);

    Pago obtenerPorId(Long id);

    //Registra el pago de una reserva pendiente
    //Si es aprobado se confirma la reserva
    Pago pagarReserva(Long afiliadoId, Long reservaId, Double valor, String numeroTarjetaSimulado);

    //Registra el pago de una membresía solicitada
    //Si es aprobado se activa
    Pago pagarMembresia(Long afiliadoId, Long membresiaId, Double valor, String numeroTarjetaSimulado);
}