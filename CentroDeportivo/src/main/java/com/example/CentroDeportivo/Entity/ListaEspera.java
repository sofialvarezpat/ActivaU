package com.example.CentroDeportivo.Entity;


import com.example.CentroDeportivo.Entity.Enum.EstadoListaEspera;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "lista_espera")
@NoArgsConstructor
public class ListaEspera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer posicion;

    @Column(name = "fecha_ingreso", nullable = false, updatable = false)
    private LocalDateTime fechaIngreso;

    //se crea la lista de espera en espera, esperando a ser liberada
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoListaEspera estado = EstadoListaEspera.EN_ESPERA;
    private LocalDateTime fechaInvitacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_id")
    private Actividad actividad;
}