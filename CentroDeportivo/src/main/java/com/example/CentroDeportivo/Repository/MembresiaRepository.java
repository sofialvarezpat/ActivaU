package com.example.CentroDeportivo.Repository;



import com.example.CentroDeportivo.Entity.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    List<Membresia> findByAfiliadoId(Long afiliadoId);
    Optional<Membresia> findByAfiliadoIdAndEstado(Long afiliadoId, String estado);
}