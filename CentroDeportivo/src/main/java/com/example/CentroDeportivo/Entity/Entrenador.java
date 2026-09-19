package com.example.CentroDeportivo.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "entrenador")
@NoArgsConstructor
public class Entrenador extends Usuario {

    @Column(length = 100)
    private String especialidad;

    @Column(length = 200)
    private String disponibilidad;
}