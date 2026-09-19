package com.example.CentroDeportivo.Repository;



import com.example.CentroDeportivo.Entity.TipoMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMembresiaRepository extends JpaRepository<TipoMembresia, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}