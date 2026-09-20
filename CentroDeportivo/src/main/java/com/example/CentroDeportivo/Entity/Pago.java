package com.example.CentroDeportivo.Entity;

import com.example.CentroDeportivo.Entity.Enum.EstadoPago;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
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
    private BigDecimal valor;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    // Referencia devuelta por la pasarela simulada.
    @Column(name = "referencia_pasarela", length = 60)
    private String referenciaPasarela;

    // Últimos dígitos de la tarjeta simulada
    @Column(name = "numero_tarjeta_simulado", length = 20)
    private String numeroTarjetaSimulado;

    // Mensaje de respuesta de la pasarela simulada
    @Column(name = "mensaje_pasarela", length = 200)
    private String mensajePasarela;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membresia_id", unique = true)
    private Membresia membresia;

    //se agrega para el RNF seguridad
    @Column(name = "tarjeta_enmascarada", length = 25)
    private String tarjetaEnmascarada;


    @Column(name = "mensaje_banco", length = 200)
    private String mensajeBanco;
}