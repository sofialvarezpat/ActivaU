package com.example.CentroDeportivo.Entity;


import com.example.CentroDeportivo.Entity.Enum.EstadoAsistencia;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "asistencia")
@NoArgsConstructor
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoAsistencia estado;

    @Column(length = 300)
    private String observacion;

    // Relación 0..1 a 1 con Reserva
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;
}