package com.example.CentroDeportivo.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concepto;
    private Double valor;
    private String numeroTarjetaSimulado;
    private String referenciaPago;
    private String estado;
    private String mensajePasarela;
    private LocalDateTime fechaPago;

    @ManyToOne
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;

    // Relación 0..1 a 1 con Reserva
    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;

    // Relación 0..1 a 1 con Membresia
    @OneToOne
    @JoinColumn(name = "membresia_id", unique = true)
    private Membresia membresia;
}