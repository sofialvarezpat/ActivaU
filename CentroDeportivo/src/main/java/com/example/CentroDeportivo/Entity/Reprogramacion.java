package com.example.CentroDeportivo.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reprogramacion")
public class Reprogramacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaAnterior;
    private LocalDate fechaNueva;
    private LocalTime horaAnterior;
    private LocalTime horaNueva;
    private String motivo;
    private LocalDateTime fechaCambio;

    @ManyToOne
    @JoinColumn(name = "actividad_id")
    private Actividad actividad;
}