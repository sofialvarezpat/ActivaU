package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.BancoRequest;
import com.example.CentroDeportivo.DTO.response.RespuestaBanco;
import com.example.CentroDeportivo.Entity.Enum.ResultadoBanco;
import com.example.CentroDeportivo.Service.BancoSimuladoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mock/banco")
@RequiredArgsConstructor
@Tag(name = "Pasarela simulada", description = "Banco de mentira (RF12). Tarjeta terminada en 0000 = sin fondos; 9999 = error de conexión")
public class BancoController {

    private final BancoSimuladoService bancoSimuladoService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Procesar una transacción simulada")
    public ResponseEntity<RespuestaBanco> procesar(@Valid @RequestBody BancoRequest request) {
        RespuestaBanco respuesta = bancoSimuladoService.procesar(request);
        HttpStatus status = respuesta.resultado() == ResultadoBanco.ERROR ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK;
        return ResponseEntity.status(status).body(respuesta);
    }
}
