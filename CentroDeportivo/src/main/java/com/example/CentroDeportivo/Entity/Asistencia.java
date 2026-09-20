package com.example.CentroDeportivo.Entity;

import com.example.CentroDeportivo.Entity.Enum.EstadoAsistencia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "asistencia")
@Getter
@Setter
@NoArgsConstructor
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoAsistencia estado;

    @Column(nullable = false)
    private LocalDateTime hora;

    @Column(length = 300)
    private String observaciones;
}
