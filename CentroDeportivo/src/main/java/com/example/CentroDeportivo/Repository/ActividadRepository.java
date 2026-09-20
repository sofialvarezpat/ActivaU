package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ActividadRepository extends JpaRepository<Actividad, Long>, JpaSpecificationExecutor<Actividad> {

    /**
     * Bloqueo pesimista (SELECT ... FOR UPDATE). Toda operación que modifique cupos, reservas
     * o lista de espera de una actividad debe tomarlo primero (riesgo R01: sobreventa).
     * Solo funciona dentro de un método @Transactional.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Actividad a where a.id = :id")
    Optional<Actividad> findByIdForUpdate(@Param("id") Long id);

    /** Actividades del entrenador que se traslapan con el horario dado (excluye canceladas y la actividad indicada). */
    @Query("""
            select count(a) from Actividad a
            where a.entrenador.id = :entrenadorId
              and a.fecha = :fecha
              and a.estado <> :cancelada
              and a.id <> :excluirId
              and a.horaInicio < :fin
              and a.horaFin > :inicio
            """)
    long contarTraslapeEntrenador(@Param("entrenadorId") Long entrenadorId,
                                  @Param("fecha") LocalDate fecha,
                                  @Param("inicio") LocalTime inicio,
                                  @Param("fin") LocalTime fin,
                                  @Param("excluirId") Long excluirId,
                                  @Param("cancelada") EstadoActividad cancelada);

    /** Actividades en el escenario que se traslapan con el horario dado (excluye canceladas y la actividad indicada). */
    @Query("""
            select count(a) from Actividad a
            where a.escenario.id = :escenarioId
              and a.fecha = :fecha
              and a.estado <> :cancelada
              and a.id <> :excluirId
              and a.horaInicio < :fin
              and a.horaFin > :inicio
            """)
    long contarTraslapeEscenario(@Param("escenarioId") Long escenarioId,
                                 @Param("fecha") LocalDate fecha,
                                 @Param("inicio") LocalTime inicio,
                                 @Param("fin") LocalTime fin,
                                 @Param("excluirId") Long excluirId,
                                 @Param("cancelada") EstadoActividad cancelada);

    List<Actividad> findByFechaBetweenAndEstadoNotOrderByFechaAscHoraInicioAsc(LocalDate desde, LocalDate hasta,
                                                                              EstadoActividad estado);
}
