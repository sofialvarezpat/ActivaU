package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.Entity.Reprogramacion;
import com.example.CentroDeportivo.Service.ServiceImp.ReprogramacionServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/reprogramacion")

public class ReprogramacionController {

    private final ReprogramacionServiceImp reprogramacionServiceImp;

    // LISTAR REPROGRAMACIONES POR ACTIVIDAD (historial de cambios)
    @GetMapping("/actividad/{actividadId}")
    public ResponseEntity<List<Reprogramacion>> listarPorActividad(@PathVariable Long actividadId) {
        List<Reprogramacion> reprogramaciones = reprogramacionServiceImp.listarPorActividad(actividadId);
        return ResponseEntity.ok(reprogramaciones);
    }

    // REPROGRAMAR UNA ACTIVIDAD
    @PostMapping("/actividad/{actividadId}")
    public ResponseEntity<Reprogramacion> reprogramar(
            @PathVariable Long actividadId,
            @RequestParam LocalDate nuevaFecha,
            @RequestParam LocalTime nuevaHoraInicio,
            @RequestParam LocalTime nuevaHoraFin,
            @RequestParam String motivo) {
        Reprogramacion reprogramacion = reprogramacionServiceImp
                .reprogramar(actividadId, nuevaFecha, nuevaHoraInicio, nuevaHoraFin, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(reprogramacion);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Reprogramacion> obtenerPorId(@PathVariable Long id) {
        Reprogramacion reprogramacion = reprogramacionServiceImp.obtenerPorId(id);
        return ResponseEntity.ok(reprogramacion);
    }
}
