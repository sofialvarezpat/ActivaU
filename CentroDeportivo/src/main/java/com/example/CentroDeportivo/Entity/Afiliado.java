package com.example.CentroDeportivo.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "afiliado")
public class Afiliado extends Usuario {

    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String contactoEmergencia;
    private String estadoPenalizacion;
}