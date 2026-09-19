package com.example.CentroDeportivo.Entity;


import com.example.CentroDeportivo.Entity.Enum.EstadoPago;
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

    @Column(length = 150)
    private String concepto;

    @Column(nullable = false, precision = 12, scale = 2)
    private Double valor;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    // Referencia devuelta por la pasarela simulada.
    @Column(name = "referencia_pasarela", length = 60)
    private String referenciaPasarela;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;

    // Relación 0..1 a 1 con Reserva
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;

    // Relación 0..1 a 1 con Membresia
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membresia_id", unique = true)
    private Membresia membresia;
}