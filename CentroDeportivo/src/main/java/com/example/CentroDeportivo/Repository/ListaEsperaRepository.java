package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.ListaEspera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {
    List<ListaEspera> findByActividadIdOrderByPosicionAsc(Long actividadId);

    //Obtiene la posición más alta el número de turno mayor ocupada actualmente

    Optional<Integer> findMaxPosicionByActividadId(Long actividadId);


     // Busca la primera persona en la lista de espera de una actividad que se encuentre.

    Optional<ListaEspera> findFirstByActividadIdAndEstadoOrderByPosicionAsc(Long actividadId, String enEspera);
}