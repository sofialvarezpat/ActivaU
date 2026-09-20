package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.EscenarioRequest;
import com.example.CentroDeportivo.DTO.response.EscenarioResponse;
import com.example.CentroDeportivo.Service.EscenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escenarios")
@RequiredArgsConstructor
@Tag(name = "Escenarios", description = "Datos maestros (RF02). Escritura solo para ADMINISTRADOR")
public class EscenarioController {

    private final EscenarioService escenarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','COORDINADOR')")
    @Operation(summary = "Listar escenarios")
    public List<EscenarioResponse> listar() {
        return escenarioService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','COORDINADOR')")
    @Operation(summary = "Consultar por id")
    public EscenarioResponse obtener(@PathVariable Long id) {
        return escenarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear")
    public EscenarioResponse crear(@Valid @RequestBody EscenarioRequest request) {
        return escenarioService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar")
    public EscenarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody EscenarioRequest request) {
        return escenarioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar")
    public void eliminar(@PathVariable Long id) {
        escenarioService.eliminar(id);
    }
}
