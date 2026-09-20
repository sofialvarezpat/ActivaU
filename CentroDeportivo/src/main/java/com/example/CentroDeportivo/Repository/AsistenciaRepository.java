package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Asistencia;
import com.example.CentroDeportivo.Entity.Enum.EstadoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    /** Fila del reporte de asistencia: registros contados por estado. */
    interface ConteoEstado {
        EstadoAsistencia getEstado();

        Long getTotal();
    }

    boolean existsByReservaId(Long reservaId);

    @Query("""
            select a.estado as estado, count(a) as total
            from Asistencia a
            where a.reserva.actividad.fecha between :desde and :hasta
            group by a.estado
            """)
    List<ConteoEstado> contarPorEstado(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
