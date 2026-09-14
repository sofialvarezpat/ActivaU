package com.example.CentroDeportivo.Repository;


import com.example.CentroDeportivo.Entity.Penalizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PenalizacionRepository extends JpaRepository<Penalizacion, Long> {
    List<Penalizacion> findByAfiliadoId(Long afiliadoId);
}