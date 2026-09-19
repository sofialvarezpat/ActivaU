package com.example.CentroDeportivo.Entity;


import com.example.CentroDeportivo.Entity.Enum.EstadoPenalizacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.EmptyStackException;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "afiliado")
@NoArgsConstructor
public class Afiliado extends Usuario {

    @Column(name = "documento_identidad", unique = true, length = 30)
    private String documentoIdentidad;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 200)
    private String direccion;

    @Column(name = "contacto_emergencia", length = 150)
    private String contactoEmergencia;

    //se crea y usa el enum para no tener estados de mas o que no concuerden, el afiliado comienza sin penalizacion
    //luego si cumple alguna condicion de penalizacion se aplicara
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_penalizacion", nullable = false,length = 20)
    private EstadoPenalizacion estadoPenalizacion = EstadoPenalizacion.SIN_PENALIZACION;
}