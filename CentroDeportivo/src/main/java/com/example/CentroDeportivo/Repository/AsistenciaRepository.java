package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    Optional<Asistencia> findByReservaId(Long reservaId);
}