package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.EntrenadorRequest;
import com.example.CentroDeportivo.DTO.request.EntrenadorUpdateRequest;
import com.example.CentroDeportivo.DTO.response.EntrenadorResponse;
import com.example.CentroDeportivo.Service.EntrenadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrenadores")
@RequiredArgsConstructor
@Tag(name = "Entrenadores", description = "Datos maestros (RF02). Escritura solo para ADMINISTRADOR")
public class EntrenadorController {

    private final EntrenadorService entrenadorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','COORDINADOR')")
    @Operation(summary = "Listar entrenadores")
    public List<EntrenadorResponse> listar() {
        return entrenadorService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','COORDINADOR')")
    @Operation(summary = "Consultar entrenador por id")
    public EntrenadorResponse obtener(@PathVariable Long id) {
        return entrenadorService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear entrenador (con su usuario de acceso)")
    public EntrenadorResponse crear(@Valid @RequestBody EntrenadorRequest request) {
        return entrenadorService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar entrenador")
    public EntrenadorResponse actualizar(@PathVariable Long id, @Valid @RequestBody EntrenadorUpdateRequest request) {
        return entrenadorService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Desactivar entrenador (no se borra, conserva su historial)")
    public void desactivar(@PathVariable Long id) {
        entrenadorService.desactivar(id);
    }
}
