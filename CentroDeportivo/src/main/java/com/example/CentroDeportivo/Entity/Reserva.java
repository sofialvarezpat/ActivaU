package com.example.CentroDeportivo.Entity;


import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;
import com.example.CentroDeportivo.Entity.Enum.OrigenCupo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Setter
@Getter
@Entity
@Table(name = "reserva")
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_reserva", nullable = false, updatable = false)
    private LocalDateTime fechaReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen_cupo", nullable = false, length = 20)
    private OrigenCupo origenCupo = OrigenCupo.DIRECTO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_id")
    private Actividad actividad;

    @PrePersist
    void prePersit(){this.fechaReserva = LocalDateTime.now(ZoneOffset.UTC);}
}