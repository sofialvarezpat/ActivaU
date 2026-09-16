package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByAfiliadoId(Long afiliadoId);
    List<Reserva> findByActividadId(Long actividadId);
    boolean existsByAfiliadoIdAndActividadId(Long afiliadoId, Long actividadId);

    List<Reserva> findByAfiliadoIdAndEstadoNot(Long afiliadoId, String estadoCancelada);
}