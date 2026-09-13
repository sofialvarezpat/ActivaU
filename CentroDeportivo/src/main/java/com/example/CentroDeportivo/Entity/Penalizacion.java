package com.example.CentroDeportivo.Entity;



import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "penalizacion")
public class Penalizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private Double valor;
    private LocalDateTime fecha;
    private String motivo;
    private Integer horasAnticipacionCancelacion;
    private LocalDateTime fechaFinBloqueo;

    @ManyToOne
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;

    @ManyToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;
}