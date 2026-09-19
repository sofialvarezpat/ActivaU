package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.ActividadRequestDTO;
import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Disciplina;
import com.example.CentroDeportivo.Entity.Entrenador;
import com.example.CentroDeportivo.Entity.Escenario;
import com.example.CentroDeportivo.Service.ActividadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadService actividadService;

    @GetMapping
    public ResponseEntity<Page<Actividad>> buscar(
            @RequestParam(required = false) Long disciplina,
            @RequestParam(required = false) Long entrenador,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) String nivel,
            Pageable pageable) {
        return ResponseEntity.ok(actividadService.buscar(disciplina, entrenador, fecha, nivel, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actividad> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.obtenerPorId(id));
    }

    @GetMapping("/{id}/cupo")
    public ResponseEntity<Boolean> tieneCupo(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.tieneCupo(id));
    }

    @PostMapping
    public ResponseEntity<Actividad> programar(@Valid @RequestBody ActividadRequestDTO dto) {
        Actividad actividad = new Actividad();
        actividad.setFecha(dto.getFecha());
        actividad.setHoraInicio(dto.getHoraInicio());
        actividad.setHoraFin(dto.getHoraFin());
        actividad.setCupoMaximo(dto.getCupoMaximo());

        Disciplina disciplina = new Disciplina();
        disciplina.setId(dto.getDisciplinaId());
        actividad.setDisciplina(disciplina);

        Entrenador entrenador = new Entrenador();
        entrenador.setId(dto.getEntrenadorId());
        actividad.setEntrenador(entrenador);

        Escenario escenario = new Escenario();
        escenario.setId(dto.getEscenarioId());
        actividad.setEscenario(escenario);

        return ResponseEntity.status(HttpStatus.CREATED).body(actividadService.programar(actividad));
    }
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<Actividad> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.finalizar(id));
    }
}