package com.example.CentroDeportivo.Entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "membresia")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInicio;
    private LocalDate fechaFin; // Tal como está en el diagrama, aunque suele ser fechaFin
    private String estado;

    @ManyToOne
    @JoinColumn(name = "tipo_membresia_id")
    private TipoMembresia tipoMembresia;

    @ManyToOne
    @JoinColumn(name = "afiliado_id")
    private Afiliado afiliado;
}