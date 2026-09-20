package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;
import com.example.CentroDeportivo.Entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /** Fila del reporte de ocupación: reservas contadas por actividad. */
    interface ConteoActividad {
        Long getActividadId();

        Long getTotal();
    }

    boolean existsByAfiliadoIdAndActividadIdAndEstadoIn(Long afiliadoId, Long actividadId, Collection<EstadoReserva> estados);

    /** Reservas del afiliado (en los estados dados) cuyo horario se traslapa con el indicado (regla de negocio 2). */
    @Query("""
            select count(r) from Reserva r
            where r.afiliado.id = :afiliadoId
              and r.estado in :estados
              and r.actividad.estado <> :actividadExcluida
              and r.actividad.fecha = :fecha
              and r.actividad.horaInicio < :fin
              and r.actividad.horaFin > :inicio
            """)
    long contarTraslape(@Param("afiliadoId") Long afiliadoId,
                        @Param("estados") Collection<EstadoReserva> estados,
                        @Param("actividadExcluida") EstadoActividad actividadExcluida,
                        @Param("fecha") LocalDate fecha,
                        @Param("inicio") LocalTime inicio,
                        @Param("fin") LocalTime fin);

    List<Reserva> findByAfiliadoIdOrderByFechaReservaDesc(Long afiliadoId);

    List<Reserva> findByActividadIdAndEstadoIn(Long actividadId, Collection<EstadoReserva> estados);

    /** Permite bloquear la actividad ANTES de cargar la reserva (evita leer datos desactualizados). */
    @Query("select r.actividad.id from Reserva r where r.id = :id")
    Optional<Long> findActividadIdById(@Param("id") Long id);

    @Query("""
            select r.actividad.id as actividadId, count(r) as total
            from Reserva r
            where r.actividad.fecha between :desde and :hasta
              and r.estado in :estados
            group by r.actividad.id
            """)
    List<ConteoActividad> contarPorActividad(@Param("desde") LocalDate desde,
                                             @Param("hasta") LocalDate hasta,
                                             @Param("estados") Collection<EstadoReserva> estados);
}
