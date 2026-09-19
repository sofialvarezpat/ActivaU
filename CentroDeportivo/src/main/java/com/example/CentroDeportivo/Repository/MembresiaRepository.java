package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Enum.EstadoMembresia;
import com.example.CentroDeportivo.Entity.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    List<Membresia> findByAfiliadoId(Long afiliadoId);

    Optional<Membresia> findByAfiliadoIdAndEstado(Long afiliadoId, EstadoMembresia estado);

    Optional<Membresia> findByAfiliadoIdAndEstadoAndFechaFinGreaterThanEqual(
            Long afiliadoId, EstadoMembresia estado, LocalDate fecha);

    List<Membresia> findByEstadoAndFechaFinLessThan(EstadoMembresia estado, LocalDate fecha);
}