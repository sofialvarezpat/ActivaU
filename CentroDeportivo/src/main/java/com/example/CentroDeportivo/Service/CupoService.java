package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.Entity.Actividad;

public interface CupoService {

    /**
     * Libera un cupo de una actividad YA BLOQUEADA (findByIdForUpdate): invita al primero de la lista
     * de espera (FIFO) o, si no hay nadie, suma un cupo disponible.
     */
    void liberarCupo(Actividad actividad);

    /** Marca como EXPIRADO las invitaciones no aceptadas a tiempo e invita al siguiente. */
    void expirarInvitacionesVencidas();
}
