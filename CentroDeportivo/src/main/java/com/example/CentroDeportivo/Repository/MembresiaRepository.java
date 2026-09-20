package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Enum.EstadoMembresia;
import com.example.CentroDeportivo.Entity.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    Optional<Membresia> findByIdAndAfiliadoId(Long id, Long afiliadoId);

    List<Membresia> findByAfiliadoIdOrderByFechaInicioDesc(Long afiliadoId);

    boolean existsByAfiliadoIdAndEstadoAndFechaFinGreaterThanEqual(Long afiliadoId, EstadoMembresia estado, LocalDate fecha);
}
