package com.example.CentroDeportivo.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer cupoMaximo;
    private Integer cuposDisponibles;
    private String estado;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "entrenador_id")
    private Entrenador entrenador;

    @ManyToOne
    @JoinColumn(name = "escenario_id")
    private Escenario escenario;
}