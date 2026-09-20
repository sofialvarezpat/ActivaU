package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Penalizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenalizacionRepository extends JpaRepository<Penalizacion, Long> {

    /** Última penalización del afiliado (la más reciente). */
    Optional<Penalizacion> findFirstByAfiliadoIdOrderByFechaDesc(Long afiliadoId);
}
