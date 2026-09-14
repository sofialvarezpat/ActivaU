package com.example.CentroDeportivo.Repository;

import com.example.CentroDeportivo.Entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByAfiliadoId(Long afiliadoId);
}