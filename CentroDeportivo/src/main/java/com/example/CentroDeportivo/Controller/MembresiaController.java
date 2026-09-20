package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.MembresiaRequest;
import com.example.CentroDeportivo.DTO.response.MembresiaResponse;
import com.example.CentroDeportivo.Service.MembresiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membresias")
@RequiredArgsConstructor
@Tag(name = "Membresías", description = "Compra de membresías con la pasarela simulada")
public class MembresiaController {

    private final MembresiaService membresiaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('AFILIADO','RECEPCIONISTA','ADMINISTRADOR')")
    @Operation(summary = "Comprar una membresía (paga con la pasarela simulada)")
    public MembresiaResponse comprar(@Valid @RequestBody MembresiaRequest request) {
        return membresiaService.comprar(request);
    }

    @GetMapping("/mias")
    @PreAuthorize("hasRole('AFILIADO')")
    @Operation(summary = "Mis membresías")
    public List<MembresiaResponse> mias() {
        return membresiaService.mias();
    }
}
