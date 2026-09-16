package com.example.CentroDeportivo.Controller;

import com.example.CentroDeportivo.Entity.TipoMembresia;
import com.example.CentroDeportivo.Service.ServiceImp.TipoMembresiaServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tipo-membresia")
public class TipoMembresiaController {

    private final TipoMembresiaServiceImp tipoMembresiaServiceImp;

    // LISTAR TODOS LOS TIPOS DE MEMBRESÍA
    @GetMapping("/obtener")
    public ResponseEntity<List<TipoMembresia>> listarTodos() {
        List<TipoMembresia> tiposMembresia = tipoMembresiaServiceImp.listarTodos();
        return ResponseEntity.ok(tiposMembresia);
    }

    // BUSCAR POR ID
    @GetMapping("/listar/{id}")
    public ResponseEntity<TipoMembresia> obtenerPorId(@PathVariable Long id) {
        TipoMembresia tipoMembresia = tipoMembresiaServiceImp.obtenerPorId(id);
        return ResponseEntity.ok(tipoMembresia);
    }

    // CREAR TIPO DE MEMBRESÍA
    @PostMapping("/crear")
    public ResponseEntity<TipoMembresia> crear(@RequestBody TipoMembresia tipoMembresia) {
        TipoMembresia nuevoTipoMembresia = tipoMembresiaServiceImp.crear(tipoMembresia);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoMembresia);
    }

    // ACTUALIZAR TIPO DE MEMBRESÍA
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<TipoMembresia> actualizar(@PathVariable Long id, @RequestBody TipoMembresia cambios) {
        TipoMembresia tipoMembresiaActualizado = tipoMembresiaServiceImp.actualizar(id, cambios);
        return ResponseEntity.ok(tipoMembresiaActualizado);
    }

    // ELIMINAR TIPO DE MEMBRESÍA
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoMembresiaServiceImp.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
