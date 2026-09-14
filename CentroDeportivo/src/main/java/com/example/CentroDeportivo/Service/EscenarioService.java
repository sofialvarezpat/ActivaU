package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Escenario;

import java.util.List;

public interface EscenarioService {

    //Busqueda normal
    List<Escenario> listarTodos();

    Escenario obtenerPorId(Long id);

    //Busqueda por disponibilidad
    List<Escenario> listarDisponibles();

    Escenario crear(Escenario escenario);

    Escenario actualizar(Long id, Escenario cambios);

    Escenario ponerEnMantenimiento(Long id);

    void eliminar(Long id);

    //Valida que un cupo propuesto para una actividad
    void validarCapacidad(Long escenarioId, Integer cupoPropuesto);
}