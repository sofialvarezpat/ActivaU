package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Reprogramacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReprogramacionRepository extends JpaRepository<Reprogramacion, Long> {
    List<Reprogramacion> findByActividadId(Long actividadId);
}
