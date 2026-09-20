package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.DisciplinaRequest;
import com.example.CentroDeportivo.DTO.response.DisciplinaResponse;
import com.example.CentroDeportivo.Service.DisciplinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disciplinas")
@RequiredArgsConstructor
@Tag(name = "Disciplinas", description = "Datos maestros (RF02). Escritura solo para ADMINISTRADOR")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar disciplinas")
    public List<DisciplinaResponse> listar() {
        return disciplinaService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar por id")
    public DisciplinaResponse obtener(@PathVariable Long id) {
        return disciplinaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear")
    public DisciplinaResponse crear(@Valid @RequestBody DisciplinaRequest request) {
        return disciplinaService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar")
    public DisciplinaResponse actualizar(@PathVariable Long id, @Valid @RequestBody DisciplinaRequest request) {
        return disciplinaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar")
    public void eliminar(@PathVariable Long id) {
        disciplinaService.eliminar(id);
    }
}
