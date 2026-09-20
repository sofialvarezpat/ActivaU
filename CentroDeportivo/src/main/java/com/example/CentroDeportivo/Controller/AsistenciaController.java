package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.AsistenciaRequest;
import com.example.CentroDeportivo.DTO.response.AsistenciaResponse;
import com.example.CentroDeportivo.Service.AsistenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@Tag(name = "Asistencia", description = "Validar asistencia e inasistencias justificadas (RF07)")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ENTRENADOR','RECEPCIONISTA')")
    @Operation(summary = "Registrar asistencia de una reserva (solo después de iniciada la clase)")
    public AsistenciaResponse registrar(@Valid @RequestBody AsistenciaRequest request) {
        return asistenciaService.registrar(request);
    }
}
