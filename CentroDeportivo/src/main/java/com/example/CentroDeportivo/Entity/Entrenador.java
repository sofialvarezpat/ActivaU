package com.example.CentroDeportivo.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "entrenador")
public class Entrenador extends Usuario {

    private String especialidad;
    private String disponibilidad;
}