package com.example.CentroDeportivo.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "asistencia")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime hora;
    private String estado;
    private String observacion;

    // Relación 0..1 a 1 con Reserva
    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;
}