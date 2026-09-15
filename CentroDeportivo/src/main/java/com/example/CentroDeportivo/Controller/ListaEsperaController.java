package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.Entity.ListaEspera;
import com.example.CentroDeportivo.Service.ListaEsperaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lista-espera")
@AllArgsConstructor
public class ListaEsperaController {

    private final ListaEsperaService listaEsperaService;

    // LISTAR POR ACTIVIDAD
    @GetMapping("/actividad/{actividadId}")
    public ResponseEntity<List<ListaEspera>> listarPorActividad(@PathVariable Long actividadId) {
        List<ListaEspera> lista = listaEsperaService.listarPorActividad(actividadId);
        return ResponseEntity.ok(lista);
    }

    // INSCRIBIR
    @PostMapping("/inscribir")
    public ResponseEntity<ListaEspera> inscribir(
            //Extraer parametros de la petición HTTP
            @RequestParam Long afiliadoId,
            @RequestParam Long actividadId) {
        ListaEspera listaEspera = listaEsperaService.inscribir(afiliadoId, actividadId);
        return ResponseEntity.status(HttpStatus.CREATED).body(listaEspera);
    }

    // INVITAR SIGUIENTE
    @PostMapping("/actividad/{actividadId}/invitar-siguiente")
    public ResponseEntity<ListaEspera> invitarSiguiente(@PathVariable Long actividadId) {
        Optional<ListaEspera> invitado = listaEsperaService.invitarSiguiente(actividadId);
        if (invitado.isPresent()) {

            return ResponseEntity.ok(invitado.get());
        } else {

            return ResponseEntity.noContent().build();
        }
    }

    // CONFIRMAR INVITACION
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ListaEspera> confirmarInvitacion(@PathVariable Long id) {
        ListaEspera listaEspera = listaEsperaService.confirmarInvitacion(id);
        return ResponseEntity.ok(listaEspera);
    }

    // EXPIRAR INVITACION
    @PatchMapping("/{id}/expirar")
    public ResponseEntity<ListaEspera> expirarInvitacion(@PathVariable Long id) {
        ListaEspera listaEspera = listaEsperaService.expirarInvitacion(id);
        return ResponseEntity.ok(listaEspera);
    }

    // OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ListaEspera> obtenerPorId(@PathVariable Long id) {
        ListaEspera listaEspera = listaEsperaService.obtenerPorId(id);
        return ResponseEntity.ok(listaEspera);
    }
}