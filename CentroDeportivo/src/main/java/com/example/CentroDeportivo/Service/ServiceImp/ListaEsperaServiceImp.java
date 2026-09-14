package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Actividad;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.ListaEspera;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Repository.ActividadRepository;
import com.example.CentroDeportivo.Repository.AfiliadoRepository;
import com.example.CentroDeportivo.Repository.ListaEsperaRepository;
import com.example.CentroDeportivo.Service.ListaEsperaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ListaEsperaServiceImp implements ListaEsperaService {

    private final ListaEsperaRepository listaEsperaRepository;
    private final AfiliadoRepository afiliadoRepository;
    private final ActividadRepository actividadRepository;

    //LISTAR POR ACTIVIDAD
    @Override
    @Transactional
    public List<ListaEspera> listarPorActividad(Long actividadId) {
        return listaEsperaRepository.findByActividadIdOrderByPosicionAsc(actividadId);
    }
    //INSCRIBIR
    @Override
    @Transactional

    public ListaEspera inscribir(Long afiliadoId, Long actividadId) {
        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Afiliado no encontrado: " + afiliadoId));
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada: " + actividadId));

        Integer siguientePosicion = listaEsperaRepository
                .findMaxPosicionByActividadId(actividadId)
                .map(p -> p + 1)
                .orElse(1);

        ListaEspera listaEspera = new ListaEspera();
        listaEspera.setAfiliado(afiliado);
        listaEspera.setActividad(actividad);
        listaEspera.setPosicion(siguientePosicion);
        listaEspera.setFechaIngreso(LocalDateTime.now());
        listaEspera.setEstado("EN_ESPERA");

        return listaEsperaRepository.save(listaEspera);
    }

    //INVITAR SIGUIENTE
    @Override
    @Transactional
    public Optional<ListaEspera> invitarSiguiente(Long actividadId) {
        Optional<ListaEspera> siguiente = listaEsperaRepository
                .findFirstByActividadIdAndEstadoOrderByPosicionAsc(actividadId, "EN_ESPERA");

        siguiente.ifPresent(le -> {
            le.setEstado("INVITADO");
            le.setFechaInvitacion(LocalDateTime.now());
            listaEsperaRepository.save(le);
        });

        return siguiente;
    }

    //CONFIRMAR INVITACION
    @Override
    @Transactional
    public ListaEspera confirmarInvitacion(Long listaEsperaId) {
        ListaEspera listaEspera = obtenerPorId(listaEsperaId);
        listaEspera.setEstado("CONFIRMADO");
        return listaEsperaRepository.save(listaEspera);
    }

    // EXPIRAR INVITACION
    @Override
    @Transactional
    public ListaEspera expirarInvitacion(Long listaEsperaId) {
        ListaEspera listaEspera = obtenerPorId(listaEsperaId);
        listaEspera.setEstado("EXPIRADO");
        return listaEsperaRepository.save(listaEspera);
    }

    //OBTENER POR ID
    @Override
    @Transactional
    public ListaEspera obtenerPorId(Long id) {
        return listaEsperaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de lista de espera no encontrado: " + id));
    }
}