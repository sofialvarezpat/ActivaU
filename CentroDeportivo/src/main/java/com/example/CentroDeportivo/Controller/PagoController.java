package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.response.PageResponse;
import com.example.CentroDeportivo.DTO.response.PagoResponse;
import com.example.CentroDeportivo.Service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Historial de pagos. Los pagos se generan al reservar o comprar una membresía")
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/mios")
    @PreAuthorize("hasRole('AFILIADO')")
    @Operation(summary = "Mis pagos (aprobados y rechazados)")
    public List<PagoResponse> mios() {
        return pagoService.mios();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA')")
    @Operation(summary = "Todos los pagos (paginado)")
    public PageResponse<PagoResponse> listar(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return pagoService.listar(page, size);
    }
}
