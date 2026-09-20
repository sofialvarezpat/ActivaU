package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Escenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscenarioRepository extends JpaRepository<Escenario, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
