package com.example.CentroDeportivo.Entity;


import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Setter
@Getter
@Entity
@Table(name = "actividad")
@NoArgsConstructor
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;


    //aplicamos el metodo EstadoActividad enum para asi tener solo los estados permitidos en la aplicación
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoActividad estado = EstadoActividad.PROGRAMADA;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entrenador_id")
    private Entrenador entrenador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "escenario_id")
    private Escenario escenario;

    //definimos el precio de las actividades dentro de ellas para evitar complejidades (0 = gratuita)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio = BigDecimal.ZERO;


    public LocalDateTime inicio(){return LocalDateTime.of(fecha, horaInicio);}
}