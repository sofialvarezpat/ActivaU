package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.ListaEspera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {
    List<ListaEspera> findByActividadIdOrderByPosicionAsc(Long actividadId);
}