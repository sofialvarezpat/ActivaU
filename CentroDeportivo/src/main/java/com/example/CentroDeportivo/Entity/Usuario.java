package com.example.CentroDeportivo.Entity;

import com.example.CentroDeportivo.Entity.Enum.EstadoUsuario;
import com.example.CentroDeportivo.Entity.Enum.Rol;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuario")
@NoArgsConstructor
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;


    @Column(unique = true, nullable = false, length = 150)
    private String correo;

    @Column(nullable = false)
    private String contraseña;

    @Column(length = 20)
    private String telefono;

    //definir rol
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    //al ingresarlo sera activo hasta que se retire
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    //esto se hace para que la fecha persista y obtenga la fecha
    @PrePersist
    void prePersist(){this.fechaRegistro = LocalDateTime.now(ZoneOffset.UTC);}
}