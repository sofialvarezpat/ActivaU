package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.DTO.request.BancoRequest;
import com.example.CentroDeportivo.DTO.request.DatosPagoRequest;
import com.example.CentroDeportivo.DTO.response.PageResponse;
import com.example.CentroDeportivo.DTO.response.PagoResponse;
import com.example.CentroDeportivo.DTO.response.RespuestaBanco;
import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoPago;
import com.example.CentroDeportivo.Entity.Enum.ResultadoBanco;
import com.example.CentroDeportivo.Entity.Pago;
import com.example.CentroDeportivo.Exception.PagoRechazadoException;
import com.example.CentroDeportivo.Repository.PagoRepository;
import com.example.CentroDeportivo.Service.BancoSimuladoService;
import com.example.CentroDeportivo.Service.PagoService;
import com.example.CentroDeportivo.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final BancoSimuladoService bancoSimuladoService;
    private final UsuarioActual usuarioActual;
    private final Clock clock;

    /**
     * IMPORTANTE: noRollbackFor. Como participa en la transacción de quien lo llama, si no lo declarara
     * aquí, Spring marcaría toda la transacción como "solo rollback" y el Pago RECHAZADO no se guardaría.
     */
    @Override
    @Transactional(noRollbackFor = PagoRechazadoException.class)
    public Pago cobrar(Afiliado afiliado, BigDecimal valor, String concepto, DatosPagoRequest datos) {
        RespuestaBanco respuesta = bancoSimuladoService.procesar(new BancoRequest(
                datos.numeroTarjeta(), datos.fechaExpiracion(), datos.cvv(), valor, null, afiliado.getId()));

        Pago pago = new Pago();
        pago.setAfiliado(afiliado);
        pago.setConcepto(concepto);
        pago.setValor(valor);
        pago.setTarjetaEnmascarada(enmascarar(datos.numeroTarjeta()));
        pago.setMensajeBanco(respuesta.mensaje());
        pago.setFechaPago(LocalDateTime.now(clock));

        if (respuesta.resultado() == ResultadoBanco.APROBADA) {
            pago.setEstado(EstadoPago.APROBADO);
            pago.setReferenciaPasarela(respuesta.referencia());
            return pagoRepository.save(pago);
        }
        pago.setEstado(EstadoPago.RECHAZADO);
        pagoRepository.save(pago);
        throw new PagoRechazadoException(respuesta.mensaje());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> mios() {
        Afiliado afiliado = usuarioActual.afiliadoActual();
        return pagoRepository.findByAfiliadoIdOrderByFechaPagoDesc(afiliado.getId())
                .stream().map(PagoResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PagoResponse> listar(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        return PageResponse.from(pagoRepository.findAllByOrderByFechaPagoDesc(pageable).map(PagoResponse::from));
    }

    private String enmascarar(String numero) {
        String limpio = numero == null ? "" : numero.replaceAll("[^0-9]", "");
        if (limpio.length() < 4) {
            return "****";
        }
        return "**** **** **** " + limpio.substring(limpio.length() - 4);
    }
}
