package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Penalizacion;
import com.example.CentroDeportivo.Entity.Reserva;

import java.time.LocalDateTime;

public interface PenalizacionService {

    /** Lanza BusinessRuleException si el afiliado tiene un bloqueo vigente; si ya se cumplió lo levanta. */
    void verificarSinPenalizacion(Afiliado afiliado);

    /** true si faltan menos horas que el límite configurado (12 h por defecto). */
    boolean esCancelacionTardia(LocalDateTime ahora, LocalDateTime inicioActividad);

    Penalizacion registrarCancelacionTardia(Afiliado afiliado, Reserva reserva, LocalDateTime ahora);
}
