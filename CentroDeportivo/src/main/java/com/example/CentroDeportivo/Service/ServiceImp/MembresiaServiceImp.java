package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoMembresia;
import com.example.CentroDeportivo.Entity.Membresia;
import com.example.CentroDeportivo.Entity.TipoMembresia;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Repository.AfiliadoRepository;
import com.example.CentroDeportivo.Repository.MembresiaRepository;
import com.example.CentroDeportivo.Repository.TipoMembresiaRepository;
import com.example.CentroDeportivo.Service.MembresiaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MembresiaServiceImp implements MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final AfiliadoRepository afiliadoRepository;
    private final TipoMembresiaRepository tipoMembresiaRepository;

    @Override
    @Transactional
    public List<Membresia> listarPorAfiliado(Long afiliadoId) {
        return membresiaRepository.findByAfiliadoId(afiliadoId);
    }

    @Override
    @Transactional
    public Membresia obtenerPorId(Long id) {
        return membresiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Membresía no encontrada: " + id));
    }

    @Override
    @Transactional
    public Membresia solicitar(Long afiliadoId, Long tipoMembresiaId) {
        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Afiliado no encontrado: " + afiliadoId));
        TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(tipoMembresiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de membresía no encontrado: " + tipoMembresiaId));

        Membresia membresia = new Membresia();
        membresia.setAfiliado(afiliado);
        membresia.setTipoMembresia(tipoMembresia);
        membresia.setEstado(EstadoMembresia.PENDIENTE_PAGO);

        return membresiaRepository.save(membresia);
    }

    @Override
    @Transactional
    public Membresia activar(Long membresiaId) {
        Membresia membresia = obtenerPorId(membresiaId);

        if (membresia.getEstado() != EstadoMembresia.PENDIENTE_PAGO) {
            throw new ConflictException(
                    "Solo se pueden activar membresías pendientes de pago (estado actual: " + membresia.getEstado() + ")");
        }

        LocalDate inicio = LocalDate.now();
        membresia.setFechaInicio(inicio);
        membresia.setFechaFin(inicio.plusDays(membresia.getTipoMembresia().getDuracionDias()));
        membresia.setEstado(EstadoMembresia.ACTIVA);

        return membresiaRepository.save(membresia);
    }

    @Override
    @Transactional
    public Optional<Membresia> obtenerMembresiaVigente(Long afiliadoId) {
        return membresiaRepository.findByAfiliadoIdAndEstadoAndFechaFinGreaterThanEqual(
                afiliadoId, EstadoMembresia.ACTIVA, LocalDate.now());
    }

    @Override
    @Transactional
    public boolean tieneMembresiaVigente(Long afiliadoId) {
        return obtenerMembresiaVigente(afiliadoId).isPresent();
    }

    @Override
    @Transactional
    public void venceMembresiasCaducadas() {
        List<Membresia> vencidas = membresiaRepository
                .findByEstadoAndFechaFinLessThan(EstadoMembresia.ACTIVA, LocalDate.now());

        vencidas.forEach(m -> m.setEstado(EstadoMembresia.VENCIDA));
        membresiaRepository.saveAll(vencidas);
    }
}