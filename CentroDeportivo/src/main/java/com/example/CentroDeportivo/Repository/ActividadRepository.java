package com.example.CentroDeportivo.Repository;


import com.example.CentroDeportivo.Entity.Actividad;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findByFecha(LocalDate fecha);
    List<Actividad> findByDisciplinaId(Long disciplinaId);
    List<Actividad> findByEntrenadorId(Long entrenadorId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Actividad a where a.id = :id")
    Optional<Actividad> findByIdForUpdate(@Param("id") Long id);
}