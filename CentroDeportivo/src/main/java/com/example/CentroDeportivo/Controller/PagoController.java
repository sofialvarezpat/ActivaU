package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.Entity.Pago;
import com.example.CentroDeportivo.Service.ServiceImp.PagoServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/pago")
public class PagoController {

    private final PagoServiceImp pagoServiceImp;

    // LISTAR PAGOS POR AFILIADO
    @GetMapping("/afiliado/{afiliadoId}")
    public ResponseEntity<List<Pago>> listarPorAfiliado(@PathVariable Long afiliadoId) {
        List<Pago> pagos = pagoServiceImp.listarPorAfiliado(afiliadoId);
        return ResponseEntity.ok(pagos);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPorId(@PathVariable Long id) {
        Pago pago = pagoServiceImp.obtenerPorId(id);
        return ResponseEntity.ok(pago);
    }

    // PAGAR UNA RESERVA
    @PostMapping("/reserva")
    public ResponseEntity<Pago> pagarReserva(
            @RequestParam Long afiliadoId,
            @RequestParam Long reservaId,
            @RequestParam Double valor,
            @RequestParam String numeroTarjetaSimulado) {
        Pago pago = pagoServiceImp.pagarReserva(afiliadoId, reservaId, valor, numeroTarjetaSimulado);
        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }

    // PAGAR UNA MEMBRESÍA
    @PostMapping("/membresia")
    public ResponseEntity<Pago> pagarMembresia(
            @RequestParam Long afiliadoId,
            @RequestParam Long membresiaId,
            @RequestParam Double valor,
            @RequestParam String numeroTarjetaSimulado) {
        Pago pago = pagoServiceImp.pagarMembresia(afiliadoId, membresiaId, valor, numeroTarjetaSimulado);
        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }
}


