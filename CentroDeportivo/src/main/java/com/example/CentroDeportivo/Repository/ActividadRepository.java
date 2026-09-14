package com.example.CentroDeportivo.Repository;


import com.example.CentroDeportivo.Entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findByFecha(LocalDate fecha);
    List<Actividad> findByDisciplinaId(Long disciplinaId);
    List<Actividad> findByEntrenadorId(Long entrenadorId);
}