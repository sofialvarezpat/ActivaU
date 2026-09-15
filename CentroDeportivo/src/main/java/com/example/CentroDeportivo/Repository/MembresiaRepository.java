package com.example.CentroDeportivo.Repository;



import com.example.CentroDeportivo.Entity.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    List<Membresia> findByAfiliadoId(Long afiliadoId);

    // Busca la membresía vigente  de un afiliado
    Optional<Membresia> findByAfiliadoIdAndEstado(Long afiliadoId, String estado);

    Optional<Membresia> findByAfiliadoIdAndEstadoAndFechaGreaterThanEqual(Long afiliadoId, String activa, LocalDate now);

    // Busca membresías activas cuya fecha de fin ya pasó, para marcarlas como vencidas
    List<Membresia> findByEstadoAndFechaLessThan(String activa, LocalDate now);
}