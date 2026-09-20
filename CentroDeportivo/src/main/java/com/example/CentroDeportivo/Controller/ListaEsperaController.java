package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.AceptarInvitacionRequest;
import com.example.CentroDeportivo.DTO.response.ListaEsperaResponse;
import com.example.CentroDeportivo.DTO.response.ReservaResponse;
import com.example.CentroDeportivo.Service.ListaEsperaService;
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
@RequestMapping("/api/lista-espera")
@RequiredArgsConstructor
@Tag(name = "Lista de espera", description = "Invitaciones FIFO cuando se libera un cupo (RF06)")
public class ListaEsperaController {

    private final ListaEsperaService listaEsperaService;
    private final ReservaService reservaService;

    @GetMapping("/mias")
    @PreAuthorize("hasRole('AFILIADO')")
    @Operation(summary = "Mis entradas en lista de espera e invitaciones")
    public List<ListaEsperaResponse> mias() {
        return listaEsperaService.mias();
    }

    @PostMapping("/{id}/aceptar")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AFILIADO')")
    @Operation(summary = "Aceptar una invitación y convertirla en reserva (pago o membresía)")
    public ReservaResponse aceptar(@PathVariable Long id,
                                   @Valid @RequestBody(required = false) AceptarInvitacionRequest request) {
        return reservaService.aceptarInvitacion(id, request);
    }
}
