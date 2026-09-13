package com.example.CentroDeportivo.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaReserva;
    private String estado;
    private String origenCupo;

    @ManyToOne
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;

    @ManyToOne
    @JoinColumn(name = "actividad_id")
    private Actividad actividad;
}