package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Reserva;

import java.util.List;

public interface ReservaService {

    // Revisado contra la entidad Reserva: sin cambios estructurales
    //Busqueda normal
    List<Reserva> listarPorAfiliado(Long afiliadoId);

    Reserva obtenerPorId(Long id);

    //Crea la reserva de un afiliado sobre una actividad
    //Si tiene membresia vigente, queda confirmada de inmediato
    //En caso contrario, pendiente de pago
    Reserva reservar(Long afiliadoId, Long actividadId);

    //Llamado por PagoService cuando se confirma el pago asociado a una reserva pendiente
    Reserva confirmarPorPago(Long reservaId);


    //Cancela una reserva, libera el cupo, invita al siguiente en lista de espera
    //Genera penalización si la cancelación fue fuera de plazo
    Reserva cancelar(Long reservaId, String motivo);

    //n afiliado no puede reservar dos actividades que se traslapen
    void validarSinTraslapeConOtrasReservas(Long afiliadoId, Actividad actividadNueva, Long reservaIdExcluir);
}