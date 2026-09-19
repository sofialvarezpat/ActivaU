package com.example.CentroDeportivo.Entity;



import com.example.CentroDeportivo.Entity.Enum.EstadoEscenario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "escenario")
@NoArgsConstructor
public class Escenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @Column(length = 150)
    private String ubicacion;

    @Column(length = 50)
    private String tipo;

    //se pone para tener estados fijos, y cada escenario tiene como estado inicial Disponible
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEscenario estado = EstadoEscenario.DISPONIBLE;
}