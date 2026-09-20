package com.example.CentroDeportivo.Controller;



import com.example.CentroDeportivo.Entity.Penalizacion;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/penalizacion")
public class PenalizacionController {

    private final PenalizacionServiceImp penalizacionServiceImp;

    // LISTAR PENALIZACIONES POR AFILIADO
    @GetMapping("/afiliado/{afiliadoId}")
    public ResponseEntity<List<Penalizacion>> listarPorAfiliado(@PathVariable Long afiliadoId) {
        List<Penalizacion> penalizaciones = penalizacionServiceImp.listarPorAfiliado(afiliadoId);
        return ResponseEntity.ok(penalizaciones);
    }

    // APLICAR PENALIZACIÓN POR CANCELACIÓN TARDÍA
    @PostMapping("/cancelacion-tardia")
    public ResponseEntity<Penalizacion> aplicarPorCancelacionTardia(
            @RequestParam Long afiliadoId,
            @RequestParam Long reservaId,
            @RequestParam int horasAnticipacion,
            @RequestParam String motivo) {
        Penalizacion penalizacion = penalizacionServiceImp
                .aplicarPorCancelacionTardia(afiliadoId, reservaId, horasAnticipacion, motivo);

        if (penalizacion == null) {
            // No se aplicó penalización porque la anticipación fue suficiente
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(penalizacion);
    }

    // APLICAR PENALIZACIÓN POR INASISTENCIA
    @PostMapping("/inasistencia")
    public ResponseEntity<Penalizacion> aplicarPorInasistencia(
            @RequestParam Long afiliadoId,
            @RequestParam String motivo) {
        Penalizacion penalizacion = penalizacionServiceImp.aplicarPorInasistencia(afiliadoId, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(penalizacion);
    }

    // LIBERAR BLOQUEOS VENCIDOS (tarea administrativa/batch)
    @PostMapping("/liberar-bloqueos-vencidos")
    public ResponseEntity<Void> liberarBloqueosVencidos() {
        penalizacionServiceImp.liberarBloqueosVencidos();
        return ResponseEntity.noContent().build();
    }
}
