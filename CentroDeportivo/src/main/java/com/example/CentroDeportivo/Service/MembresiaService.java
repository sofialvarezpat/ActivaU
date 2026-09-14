package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Membresia;

import java.util.List;
import java.util.Optional;

public interface MembresiaService {

    //Busqueda normal
    List<Membresia> listarPorAfiliado(Long afiliadoId);

    Membresia obtenerPorId(Long id);

    //Crea una membresía en estado pendiente de pago
    //Se activa cuando PagoService, confirma el pago asociado
    Membresia solicitar(Long afiliadoId, Long tipoMembresiaId);

    Membresia activar(Long membresiaId);

    //La membresía solo habilita actividades durante su vigencia
    Optional<Membresia> obtenerMembresiaVigente(Long afiliadoId);

    //Boolean porque la membresia esta vigente o no
    boolean tieneMembresiaVigente(Long afiliadoId);

    //Marca como vencidas las membresías cuya fecha de fin ya pasó.
    void venceMembresiasCaducadas();
}