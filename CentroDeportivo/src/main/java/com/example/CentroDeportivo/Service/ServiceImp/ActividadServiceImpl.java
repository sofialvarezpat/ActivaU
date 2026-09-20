package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.ActividadRequest;
import com.example.CentroDeportivo.DTO.request.CancelarActividadRequest;
import com.example.CentroDeportivo.DTO.request.FiltroActividades;
import com.example.CentroDeportivo.DTO.request.ReprogramarRequest;
import com.example.CentroDeportivo.DTO.response.ActividadResponse;
import com.example.CentroDeportivo.DTO.response.CupoResponse;
import com.example.CentroDeportivo.DTO.response.PageResponse;
import com.example.CentroDeportivo.Entity.*;
import com.example.CentroDeportivo.Entity.Enum.*;
import com.example.CentroDeportivo.Exception.BusinessRuleException;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.*;
import com.example.CentroDeportivo.Service.ActividadService;
import com.example.CentroDeportivo.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

    private static final List<EstadoReserva> RESERVA_ACTIVA = List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);
    private static final List<EstadoListaEspera> EN_FILA = List.of(EstadoListaEspera.EN_ESPERA, EstadoListaEspera.INVITADO);

    private final ActividadRepository actividadRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final EscenarioRepository escenarioRepository;
    private final ReprogramacionRepository reprogramacionRepository;
    private final ReservaRepository reservaRepository;
    private final ListaEsperaRepository listaEsperaRepository;
    private final UsuarioActual usuarioActual;
    private final Clock clock;

    // ------------------------------------------------------------------ RF03
    @Override
    @Transactional
    public ActividadResponse programar(ActividadRequest req) {
        Disciplina disciplina = disciplinaRepository.findById(req.disciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina no encontrada"));
        Entrenador entrenador = entrenadorRepository.findById(req.entrenadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado"));
        Escenario escenario = escenarioRepository.findById(req.escenarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Escenario no encontrado"));

        if (entrenador.getEstado() != EstadoUsuario.ACTIVO) {
            throw new BusinessRuleException("El entrenador está inactivo");
        }
        if (escenario.getEstado() != EstadoEscenario.DISPONIBLE) {
            throw new BusinessRuleException("El escenario no está disponible (en mantenimiento)");
        }
        validarHorario(req.fecha(), req.horaInicio(), req.horaFin());
        if (req.cupoMaximo() > escenario.getCapacidadMaxima()) {
            throw new BusinessRuleException("El cupo máximo (" + req.cupoMaximo()
                    + ") supera la capacidad del escenario (" + escenario.getCapacidadMaxima() + ")");
        }
        validarDisponibilidad(entrenador.getId(), escenario.getId(), req.fecha(), req.horaInicio(), req.horaFin(), 0L);

        Actividad a = new Actividad();
        a.setDisciplina(disciplina);
        a.setEntrenador(entrenador);
        a.setEscenario(escenario);
        a.setFecha(req.fecha());
        a.setHoraInicio(req.horaInicio());
        a.setHoraFin(req.horaFin());
        a.setCupoMaximo(req.cupoMaximo());
        a.setCuposDisponibles(req.cupoMaximo());
        a.setPrecio(req.precio());
        a.setEstado(EstadoActividad.PROGRAMADA);
        return ActividadResponse.from(actividadRepository.save(a));
    }

    // ------------------------------------------------------------------ RF04
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActividadResponse> listar(FiltroActividades filtro, int page, int size) {
        Long entrenadorId = filtro.entrenadorId();
        if (usuarioActual.tieneRol(Rol.ENTRENADOR)) {
            entrenadorId = usuarioActual.obtener().getId(); // el entrenador solo ve su agenda
        }

        Specification<Actividad> spec = (root, query, cb) -> cb.conjunction();

        if (filtro.disciplina() != null && !filtro.disciplina().isBlank()) {
            String patron = "%" + filtro.disciplina().trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("disciplina").get("nombre")), patron));
        }
        if (entrenadorId != null) {
            Long id = entrenadorId;
            spec = spec.and((root, query, cb) -> cb.equal(root.get("entrenador").get("id"), id));
        }
        if (filtro.nivel() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("disciplina").get("nivel"), filtro.nivel()));
        }
        if (filtro.estado() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), filtro.estado()));
        }
        if (filtro.fecha() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.<LocalDate>get("fecha"), filtro.fecha()));
        } else if (!filtro.incluirPasadas()) {
            LocalDate hoy = LocalDate.now(clock);
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.<LocalDate>get("fecha"), hoy));
        }

        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by("fecha", "horaInicio"));
        Page<Actividad> pagina = actividadRepository.findAll(spec, pageable);
        return PageResponse.from(pagina.map(ActividadResponse::from));
    }

    @Override
    @Transactional(readOnly = true)
    public ActividadResponse obtener(Long id) {
        return ActividadResponse.from(buscar(id));
    }

    // ------------------------------------------------------------------ RF08
    @Override
    @Transactional
    public ActividadResponse reprogramar(Long id, ReprogramarRequest req) {
        Actividad a = actividadRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
        if (a.getEstado() == EstadoActividad.CANCELADA) {
            throw new BusinessRuleException("No se puede reprogramar una actividad cancelada");
        }
        if (!LocalDateTime.of(a.getFecha(), a.getHoraInicio()).isAfter(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("No se puede reprogramar una actividad que ya inició o finalizó");
        }
        if (a.getEscenario().getEstado() != EstadoEscenario.DISPONIBLE) {
            throw new BusinessRuleException("El escenario no está disponible (en mantenimiento)");
        }
        validarHorario(req.nuevaFecha(), req.nuevaHoraInicio(), req.nuevaHoraFin());
        validarDisponibilidad(a.getEntrenador().getId(), a.getEscenario().getId(),
                req.nuevaFecha(), req.nuevaHoraInicio(), req.nuevaHoraFin(), a.getId());

        // El registro original NO se borra: queda en la tabla reprogramacion (historial).
        Reprogramacion historial = new Reprogramacion();
        historial.setActividad(a);
        historial.setFechaAnterior(a.getFecha());
        historial.setHoraAnterior(a.getHoraInicio());
        historial.setFechaNueva(req.nuevaFecha());
        historial.setHoraNueva(req.nuevaHoraInicio());
        historial.setMotivo(req.motivo());
        historial.setFechaCambio(LocalDateTime.now(clock));
        reprogramacionRepository.save(historial);

        a.setFecha(req.nuevaFecha());
        a.setHoraInicio(req.nuevaHoraInicio());
        a.setHoraFin(req.nuevaHoraFin());
        a.setEstado(EstadoActividad.REPROGRAMADA);
        actividadRepository.save(a);

        listaEsperaRepository.findByActividadIdAndEstadoIn(a.getId(), EN_FILA).forEach(e ->
                log.info("Notificación (simulada) al afiliado {}: la actividad {} fue reprogramada",
                        e.getAfiliado().getId(), a.getId()));
        return ActividadResponse.from(a);
    }

    @Override
    @Transactional
    public ActividadResponse cancelar(Long id, CancelarActividadRequest req) {
        Actividad a = actividadRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
        if (a.getEstado() == EstadoActividad.CANCELADA) {
            throw new ConflictException("La actividad ya estaba cancelada");
        }
        if (!LocalDateTime.of(a.getFecha(), a.getHoraInicio()).isAfter(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("No se puede cancelar una actividad que ya inició o finalizó");
        }

        Reprogramacion historial = new Reprogramacion();
        historial.setActividad(a);
        historial.setFechaAnterior(a.getFecha());
        historial.setHoraAnterior(a.getHoraInicio());
        historial.setMotivo(req.motivo());
        historial.setFechaCambio(LocalDateTime.now(clock));
        reprogramacionRepository.save(historial);

        a.setEstado(EstadoActividad.CANCELADA);
        actividadRepository.save(a);

        // Las reservas se cancelan SIN penalización y la lista de espera pierde sentido.
        reservaRepository.findByActividadIdAndEstadoIn(a.getId(), RESERVA_ACTIVA)
                .forEach(r -> r.setEstado(EstadoReserva.CANCELADA));
        for (ListaEspera e : listaEsperaRepository.findByActividadIdAndEstadoIn(a.getId(), EN_FILA)) {
            e.setEstado(EstadoListaEspera.EXPIRADO);
            log.info("Notificación (simulada) al afiliado {}: la actividad {} fue cancelada",
                    e.getAfiliado().getId(), a.getId());
        }
        return ActividadResponse.from(a);
    }

    // ------------------------------------------------------------------ RF06
    @Override
    @Transactional(readOnly = true)
    public CupoResponse cupo(Long id) {
        Actividad a = buscar(id);
        long enEspera = listaEsperaRepository.countByActividadIdAndEstado(id, EstadoListaEspera.EN_ESPERA);

        Integer miPosicion = null;
        EstadoListaEspera miEstado = null;
        if (usuarioActual.tieneRol(Rol.AFILIADO)) {
            Long afiliadoId = usuarioActual.obtener().getId();
            Optional<ListaEspera> mia = listaEsperaRepository.findByActividadIdAndAfiliadoIdAndEstadoIn(id, afiliadoId, EN_FILA);
            if (mia.isPresent()) {
                miEstado = mia.get().getEstado();
                if (miEstado == EstadoListaEspera.EN_ESPERA) {
                    miPosicion = (int) listaEsperaRepository.countByActividadIdAndEstadoAndPosicionLessThan(
                            id, EstadoListaEspera.EN_ESPERA, mia.get().getPosicion()) + 1;
                }
            }
        }
        return new CupoResponse(a.getId(), a.getCupoMaximo(), a.getCuposDisponibles(), enEspera, miPosicion, miEstado);
    }

    // ------------------------------------------------------------------ helpers
    private Actividad buscar(Long id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    }

    private void validarHorario(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        if (!fin.isAfter(inicio)) {
            throw new BusinessRuleException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (!LocalDateTime.of(fecha, inicio).isAfter(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("La actividad debe programarse en una fecha y hora futuras");
        }
    }

    private void validarDisponibilidad(Long entrenadorId, Long escenarioId, LocalDate fecha,
                                       LocalTime inicio, LocalTime fin, Long excluirActividadId) {
        if (actividadRepository.contarTraslapeEntrenador(entrenadorId, fecha, inicio, fin,
                excluirActividadId, EstadoActividad.CANCELADA) > 0) {
            throw new ConflictException("El entrenador ya tiene una actividad en ese horario");
        }
        if (actividadRepository.contarTraslapeEscenario(escenarioId, fecha, inicio, fin,
                excluirActividadId, EstadoActividad.CANCELADA) > 0) {
            throw new ConflictException("El escenario ya está ocupado en ese horario");
        }
    }
}
