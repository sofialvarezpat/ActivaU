package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Disciplina;

import java.util.List;

public interface DisciplinaService {

    //Busqueda normal
    List<Disciplina> listarTodas();

    Disciplina obtenerPorId(Long id);

    //Busqueda por nivel
    List<Disciplina> buscarPorNivel(String nivel);

    Disciplina crear(Disciplina disciplina);

    Disciplina actualizar(Long id, Disciplina cambios);

    void eliminar(Long id);
}