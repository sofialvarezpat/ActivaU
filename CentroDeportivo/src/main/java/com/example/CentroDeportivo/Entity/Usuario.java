package com.example.CentroDeportivo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuario")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;

    @Column(unique = true)
    private String correo;

    private String contraseña;
    private String telefono;
    private String rol;
    private String estado;
    private LocalDateTime fechaRegistro;
}