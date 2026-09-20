package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Enum.EstadoPago;
import com.example.CentroDeportivo.Entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByAfiliadoIdOrderByFechaPagoDesc(Long afiliadoId);

    Page<Pago> findAllByOrderByFechaPagoDesc(Pageable pageable);

    Optional<Pago> findFirstByReservaIdAndEstado(Long reservaId, EstadoPago estado);

    /** Suma de pagos ligados a reservas en el rango [desde, hasta). Devuelve null si no hay pagos. */
    @Query("""
            select sum(p.valor) from Pago p
            where p.estado = :estado and p.reserva is not null
              and p.fechaPago >= :desde and p.fechaPago < :hasta
            """)
    BigDecimal sumaPorReservas(@Param("estado") EstadoPago estado,
                               @Param("desde") LocalDateTime desde,
                               @Param("hasta") LocalDateTime hasta);

    /** Suma de pagos ligados a membresías en el rango [desde, hasta). Devuelve null si no hay pagos. */
    @Query("""
            select sum(p.valor) from Pago p
            where p.estado = :estado and p.membresia is not null
              and p.fechaPago >= :desde and p.fechaPago < :hasta
            """)
    BigDecimal sumaPorMembresias(@Param("estado") EstadoPago estado,
                                 @Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta);

    @Query("""
            select count(p) from Pago p
            where p.estado = :estado and p.fechaPago >= :desde and p.fechaPago < :hasta
            """)
    long contarPorEstado(@Param("estado") EstadoPago estado,
                         @Param("desde") LocalDateTime desde,
                         @Param("hasta") LocalDateTime hasta);
}
