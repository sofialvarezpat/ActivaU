package com.example.CentroDeportivo.Entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_membresia")
public class TipoMembresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private Integer duracionDias;
    private String beneficios;
}