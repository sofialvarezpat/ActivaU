package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.Entity.Membresia;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/membresia")
public class MembresiaController {

    private final MembresiaServiceImp membresiaServiceImp;

    // LISTAR MEMBRESÍAS POR AFILIADO
    @GetMapping("/afiliado/{afiliadoId}")
    public ResponseEntity<List<Membresia>> listarPorAfiliado(@PathVariable Long afiliadoId) {
        List<Membresia> membresias = membresiaServiceImp.listarPorAfiliado(afiliadoId);
        return ResponseEntity.ok(membresias);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Membresia> obtenerPorId(@PathVariable Long id) {
        Membresia membresia = membresiaServiceImp.obtenerPorId(id);
        return ResponseEntity.ok(membresia);
    }

    // SOLICITAR MEMBRESÍA (queda en estado pendiente de pago)
    @PostMapping("/solicitar")
    public ResponseEntity<Membresia> solicitar(
            @RequestParam Long afiliadoId,
            @RequestParam Long tipoMembresiaId) {
        Membresia nuevaMembresia = membresiaServiceImp.solicitar(afiliadoId, tipoMembresiaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMembresia);
    }

    // ACTIVAR MEMBRESÍA (normalmente invocado tras confirmar el pago)
    @PatchMapping("/{membresiaId}/activar")
    public ResponseEntity<Membresia> activar(@PathVariable Long membresiaId) {
        Membresia membresiaActivada = membresiaServiceImp.activar(membresiaId);
        return ResponseEntity.ok(membresiaActivada);
    }

    // OBTENER MEMBRESÍA VIGENTE DE UN AFILIADO
    @GetMapping("/afiliado/{afiliadoId}/vigente")
    public ResponseEntity<Membresia> obtenerMembresiaVigente(@PathVariable Long afiliadoId) {
        Optional<Membresia> membresia = membresiaServiceImp.obtenerMembresiaVigente(afiliadoId);
        return membresia
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // VERIFICAR SI UN AFILIADO TIENE MEMBRESÍA VIGENTE
    @GetMapping("/afiliado/{afiliadoId}/tiene_vigente")
    public ResponseEntity<Boolean> tieneMembresiaVigente(@PathVariable Long afiliadoId) {
        boolean tieneVigente = membresiaServiceImp.tieneMembresiaVigente(afiliadoId);
        return ResponseEntity.ok(tieneVigente);
    }

    // MARCAR COMO VENCIDAS LAS MEMBRESÍAS CADUCADAS (tarea administrativa/batch)
    @PostMapping("/vencer_caducadas")
    public ResponseEntity<Void> venceMembresiasCaducadas() {
        membresiaServiceImp.venceMembresiasCaducadas();
        return ResponseEntity.noContent().build();
    }
}