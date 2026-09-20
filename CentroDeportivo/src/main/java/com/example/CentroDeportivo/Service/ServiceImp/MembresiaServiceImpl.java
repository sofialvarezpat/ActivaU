package com.example.CentroDeportivo.Service.ServiceImp;


import com.example.CentroDeportivo.DTO.request.MembresiaRequest;
import com.example.CentroDeportivo.DTO.response.MembresiaResponse;
import com.example.CentroDeportivo.DTO.response.PagoResponse;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoMembresia;
import com.example.CentroDeportivo.Entity.Membresia;
import com.example.CentroDeportivo.Entity.Pago;
import com.example.CentroDeportivo.Entity.TipoMembresia;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.PagoRechazadoException;
import com.example.CentroDeportivo.Exception.ResourceNotFoundException;
import com.example.CentroDeportivo.Repository.MembresiaRepository;
import com.example.CentroDeportivo.Repository.TipoMembresiaRepository;
import com.example.CentroDeportivo.Service.MembresiaService;
import com.example.CentroDeportivo.Service.PagoService;
import com.example.CentroDeportivo.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembresiaServiceImpl implements MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final TipoMembresiaRepository tipoMembresiaRepository;
    private final PagoService pagoService;
    private final UsuarioActual usuarioActual;
    private final Clock clock;

    /** Diagrama de actividades "Membresia": pagar -> si aprueban, crear membresía y calcular fechaFin. */
    @Override
    @Transactional(noRollbackFor = PagoRechazadoException.class)
    public MembresiaResponse comprar(MembresiaRequest req) {
        Afiliado afiliado = usuarioActual.resolverAfiliado(req.afiliadoId());
        TipoMembresia tipo = tipoMembresiaRepository.findById(req.tipoMembresiaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de membresía no encontrado"));

        LocalDate hoy = LocalDate.now(clock);
        if (membresiaRepository.existsByAfiliadoIdAndEstadoAndFechaFinGreaterThanEqual(
                afiliado.getId(), EstadoMembresia.ACTIVA, hoy)) {
            throw new ConflictException("El afiliado ya tiene una membresía vigente");
        }

        Pago pago = pagoService.cobrar(afiliado, tipo.getPrecio(), "Membresía " + tipo.getNombre(), req.datosPago());

        Membresia m = new Membresia();
        m.setAfiliado(afiliado);
        m.setTipoMembresia(tipo);
        m.setFechaInicio(hoy);
        m.setFechaFin(hoy.plusDays(tipo.getDuracionDias()));
        m.setEstado(EstadoMembresia.ACTIVA);
        m = membresiaRepository.save(m);
        pago.setMembresia(m);

        return MembresiaResponse.from(m, hoy, PagoResponse.from(pago));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembresiaResponse> mias() {
        Afiliado afiliado = usuarioActual.afiliadoActual();
        LocalDate hoy = LocalDate.now(clock);
        return membresiaRepository.findByAfiliadoIdOrderByFechaInicioDesc(afiliado.getId())
                .stream().map(m -> MembresiaResponse.from(m, hoy, null)).toList();
    }
}
