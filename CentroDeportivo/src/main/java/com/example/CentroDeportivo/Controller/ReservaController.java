package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.Entity.Reserva;
import com.example.CentroDeportivo.Service.ServiceImp.ReservaServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/reserva")
public class ReservaController {

    private final ReservaServiceImp reservaServiceImp;

    // LISTAR RESERVAS POR AFILIADO
    @GetMapping("/afiliado/{afiliadoId}")
    public ResponseEntity<List<Reserva>> listarPorAfiliado(@PathVariable Long afiliadoId) {
        List<Reserva> reservas = reservaServiceImp.listarPorAfiliado(afiliadoId);
        return ResponseEntity.ok(reservas);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(@PathVariable Long id) {
        Reserva reserva = reservaServiceImp.obtenerPorId(id);
        return ResponseEntity.ok(reserva);
    }

    // CREAR RESERVA
    @PostMapping
    public ResponseEntity<Reserva> reservar(
            @RequestParam Long afiliadoId,
            @RequestParam Long actividadId) {
        Reserva reserva = reservaServiceImp.reservar(afiliadoId, actividadId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    // CONFIRMAR RESERVA POR PAGO (normalmente invocado desde PagoService)
    @PatchMapping("/{reservaId}/confirmar-por-pago")
    public ResponseEntity<Reserva> confirmarPorPago(@PathVariable Long reservaId) {
        Reserva reserva = reservaServiceImp.confirmarPorPago(reservaId);
        return ResponseEntity.ok(reserva);
    }

    // CANCELAR RESERVA
    @PatchMapping("/{reservaId}/cancelar")
    public ResponseEntity<Reserva> cancelar(
            @PathVariable Long reservaId,
            @RequestParam String motivo) {
        Reserva reserva = reservaServiceImp.cancelar(reservaId, motivo);
        return ResponseEntity.ok(reserva);
    }
}
