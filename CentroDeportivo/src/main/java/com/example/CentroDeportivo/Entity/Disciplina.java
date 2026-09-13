package com.example.CentroDeportivo.Entity;



import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "disciplina")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String nivel;
}