package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.DTO.request.ActividadRequest;
import com.example.CentroDeportivo.DTO.request.CancelarActividadRequest;
import com.example.CentroDeportivo.DTO.request.FiltroActividades;
import com.example.CentroDeportivo.DTO.request.ReprogramarRequest;
import com.example.CentroDeportivo.DTO.response.*;
import com.example.CentroDeportivo.Entity.Enum.EstadoActividad;
import com.example.CentroDeportivo.Entity.Enum.NivelDisciplina;
import com.example.CentroDeportivo.Service.ActividadService;
import com.example.CentroDeportivo.Service.ListaEsperaService;
import com.example.CentroDeportivo.Service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
@Tag(name = "Actividades", description = "Programación, agenda, cupos y lista de espera (RF03, RF04, RF06, RF08)")
public class ActividadController {

    private final ActividadService actividadService;
    private final ListaEsperaService listaEsperaService;
    private final ReservaService reservaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COORDINADOR','ADMINISTRADOR')")
    @Operation(summary = "Programar actividad (RF03)")
    public ActividadResponse programar(@Valid @RequestBody ActividadRequest request) {
        return actividadService.programar(request);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar oferta con filtros y paginación (RF04)",
            description = "Por defecto solo muestra actividades desde hoy. El ENTRENADOR solo ve su propia agenda.")
    public PageResponse<ActividadResponse> listar(
            @RequestParam(required = false) String disciplina,
            @RequestParam(required = false) Long entrenadorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) NivelDisciplina nivel,
            @RequestParam(required = false) EstadoActividad estado,
            @RequestParam(defaultValue = "false") boolean incluirPasadas,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return actividadService.listar(
                new FiltroActividades(disciplina, entrenadorId, fecha, nivel, estado, incluirPasadas), page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar una actividad")
    public ActividadResponse obtener(@PathVariable Long id) {
        return actividadService.obtener(id);
    }

    @PatchMapping("/{id}/reprogramar")
    @PreAuthorize("hasAnyRole('COORDINADOR','ADMINISTRADOR')")
    @Operation(summary = "Reprogramar actividad conservando el historial (RF08)")
    public ActividadResponse reprogramar(@PathVariable Long id, @Valid @RequestBody ReprogramarRequest request) {
        return actividadService.reprogramar(id, request);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('COORDINADOR','ADMINISTRADOR')")
    @Operation(summary = "Cancelar actividad conservando el historial (RF08)")
    public ActividadResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarActividadRequest request) {
        return actividadService.cancelar(id, request);
    }

    @GetMapping("/{id}/cupo")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar cupos y estado de la lista de espera (RF06)")
    public CupoResponse cupo(@PathVariable Long id) {
        return actividadService.cupo(id);
    }

    @PostMapping("/{id}/lista-espera")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AFILIADO')")
    @Operation(summary = "Unirse a la lista de espera de una actividad llena (RF06)")
    public ListaEsperaResponse unirseListaEspera(@PathVariable Long id) {
        return listaEsperaService.unirse(id);
    }

    @GetMapping("/{id}/reservas")
    @PreAuthorize("hasAnyRole('ENTRENADOR','RECEPCIONISTA','COORDINADOR','ADMINISTRADOR')")
    @Operation(summary = "Afiliados con reserva confirmada de una actividad (para tomar asistencia)")
    public List<ReservaResponse> reservas(@PathVariable Long id) {
        return reservaService.porActividad(id);
    }
}
