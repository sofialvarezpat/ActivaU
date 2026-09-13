package com.example.CentroDeportivo.Entity;



import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "escenario")
public class Escenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer capacidadMaxima;
    private String ubicacion;
    private String tipo;
    private String estado;
}