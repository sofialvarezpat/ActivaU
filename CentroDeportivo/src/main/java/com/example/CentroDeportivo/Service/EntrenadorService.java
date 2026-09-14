package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Entrenador;

import java.util.List;

public interface EntrenadorService {

    //Busqueda normal
    List<Entrenador> listarTodos();

    Entrenador obtenerPorId(Long id);

    List<Entrenador> buscarPorEspecialidad(String especialidad);

    Entrenador registrar(Entrenador entrenador);

    Entrenador actualizar(Long id, Entrenador cambios);

    void desactivar(Long id);
}