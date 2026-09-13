package com.example.CentroDeportivo.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lista_espera")
public class ListaEspera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer posicion;
    private LocalDateTime fechaIngreso;
    private String estado;
    private LocalDateTime fechaInvitacion;

    @ManyToOne
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;

    @ManyToOne
    @JoinColumn(name = "actividad_id")
    private Actividad actividad;
}