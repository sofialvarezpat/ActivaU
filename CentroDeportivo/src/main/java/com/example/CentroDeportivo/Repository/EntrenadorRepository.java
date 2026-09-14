package com.example.CentroDeportivo.Repository;



import com.example.CentroDeportivo.Entity.Entrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {
    List<Entrenador> findByEspecialidad(String especialidad);
}
