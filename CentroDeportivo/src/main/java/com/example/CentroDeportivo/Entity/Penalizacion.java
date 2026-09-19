package com.example.CentroDeportivo.Entity;

import com.example.CentroDeportivo.Entity.Enum.TipoPenalizacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "penalizacion")
@NoArgsConstructor
public class Penalizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private TipoPenalizacion tipo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 300)
    private String motivo;

    @Column(name = "fecha_fin_bloqueo")
    private LocalDateTime fechaFinBloqueo;

    @Column
    private Double valor;

    @Column(name = "horas_anticipacion_cancelacion")
    private Integer horasAnticipacionCancelacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;
}