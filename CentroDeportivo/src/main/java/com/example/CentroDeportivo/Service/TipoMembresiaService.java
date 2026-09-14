package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.TipoMembresia;

import java.util.List;

public interface TipoMembresiaService {

    //Busqueda normal
    List<TipoMembresia> listarTodos();

    TipoMembresia obtenerPorId(Long id);

    TipoMembresia crear(TipoMembresia tipoMembresia);

    TipoMembresia actualizar(Long id, TipoMembresia cambios);

    void eliminar(Long id);
}