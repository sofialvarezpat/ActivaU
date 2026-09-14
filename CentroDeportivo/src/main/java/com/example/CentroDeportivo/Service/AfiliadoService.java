package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Afiliado;

import java.util.List;

public interface AfiliadoService {

    //Busqueda normal
    List<Afiliado> listarTodos();

    Afiliado obtenerPorId(Long id);

    Afiliado obtenerPorCorreo(String correo);

    Afiliado obtenerPorDocumento(String documentoIdentidad);

    Afiliado registrar(Afiliado afiliado);

    Afiliado actualizar(Long id, Afiliado cambios);

    void desactivar(Long id);

    void validarSinBloqueo(Long afiliadoId);
}