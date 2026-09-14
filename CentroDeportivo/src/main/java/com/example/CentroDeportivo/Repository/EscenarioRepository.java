package com.example.CentroDeportivo.Repository;



import com.example.CentroDeportivo.Entity.Escenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscenarioRepository extends JpaRepository<Escenario, Long> {
}