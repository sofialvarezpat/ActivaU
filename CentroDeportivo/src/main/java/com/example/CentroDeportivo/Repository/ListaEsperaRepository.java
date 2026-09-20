package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Enum.EstadoListaEspera;
import com.example.CentroDeportivo.Entity.ListaEspera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {

    /** Siguiente en la fila (FIFO) para una actividad. */
    Optional<ListaEspera> findFirstByActividadIdAndEstadoOrderByPosicionAsc(Long actividadId, EstadoListaEspera estado);

    boolean existsByActividadIdAndAfiliadoIdAndEstadoIn(Long actividadId, Long afiliadoId, Collection<EstadoListaEspera> estados);

    Optional<ListaEspera> findByActividadIdAndAfiliadoIdAndEstadoIn(Long actividadId, Long afiliadoId,
                                                                   Collection<EstadoListaEspera> estados);

    List<ListaEspera> findByActividadIdAndEstadoIn(Long actividadId, Collection<EstadoListaEspera> estados);

    long countByActividadIdAndEstado(Long actividadId, EstadoListaEspera estado);

    /** Cuántos hay en la misma fila delante de la posición dada (para calcular "mi posición"). */
    long countByActividadIdAndEstadoAndPosicionLessThan(Long actividadId, EstadoListaEspera estado, Integer posicion);

    List<ListaEspera> findByAfiliadoIdOrderByFechaIngresoDesc(Long afiliadoId);

    @Query("select coalesce(max(l.posicion), 0) from ListaEspera l where l.actividad.id = :actividadId")
    Integer maxPosicion(@Param("actividadId") Long actividadId);

    /** Permite bloquear la actividad ANTES de cargar la entrada (evita leer datos desactualizados). */
    @Query("select l.actividad.id from ListaEspera l where l.id = :id")
    Optional<Long> findActividadIdById(@Param("id") Long id);

    /** Invitaciones enviadas antes del límite (ya vencidas). */
    @Query("select l.id from ListaEspera l where l.estado = :estado and l.fechaInvitacion < :limite")
    List<Long> findIdsVencidos(@Param("estado") EstadoListaEspera estado, @Param("limite") LocalDateTime limite);
}
