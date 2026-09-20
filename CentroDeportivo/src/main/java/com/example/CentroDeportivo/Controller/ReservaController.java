package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.ReservaRequest;
import com.example.CentroDeportivo.DTO.response.CancelacionReservaResponse;
import com.example.CentroDeportivo.DTO.response.ReservaResponse;
import com.example.CentroDeportivo.Service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Reservar y pagar, cancelar con penalización (RF05, RF11)")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('AFILIADO','RECEPCIONISTA','ADMINISTRADOR')")
    @Operation(summary = "Reservar una actividad pagando o usando una membresía vigente (RF05)",
            description = "Errores: 409 sin cupos / traslape / reserva duplicada, 402 pago rechazado, 400 membresía vencida o afiliado penalizado.")
    public ReservaResponse reservar(@Valid @RequestBody ReservaRequest request) {
        return reservaService.reservar(request);
    }

    @GetMapping("/mias")
    @PreAuthorize("hasRole('AFILIADO')")
    @Operation(summary = "Mis reservas")
    public List<ReservaResponse> mias() {
        return reservaService.mias();
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('AFILIADO','RECEPCIONISTA','ADMINISTRADOR')")
    @Operation(summary = "Cancelar una reserva; con menos de 12 h de anticipación penaliza (RF11)")
    public CancelacionReservaResponse cancelar(@PathVariable Long id) {
        return reservaService.cancelar(id);
    }
}
