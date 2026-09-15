package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Pago;
import com.example.CentroDeportivo.Service.PagoService;

import java.util.List;

import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Membresia;
import com.example.CentroDeportivo.Entity.Pago;
import com.example.CentroDeportivo.Entity.Reserva;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Repository.AfiliadoRepository;
import com.example.CentroDeportivo.Repository.MembresiaRepository;
import com.example.CentroDeportivo.Repository.PagoRepository;
import com.example.CentroDeportivo.Repository.ReservaRepository;
import com.example.CentroDeportivo.Service.MembresiaService;
import com.example.CentroDeportivo.Service.PagoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PagoServiceImp implements PagoService {

    private final PagoRepository pagoRepository;
    private final AfiliadoRepository afiliadoRepository;
    private final ReservaRepository reservaRepository;
    private final MembresiaRepository membresiaRepository;
    private final MembresiaService membresiaService;

    @Override
    @Transactional
    public List<Pago> listarPorAfiliado(Long afiliadoId) {
        return pagoRepository.findByAfiliadoId(afiliadoId);
    }

    @Override
    @Transactional
    public Pago obtenerPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado: " + id));
    }

    @Override
    @Transactional
    public Pago pagarReserva(Long afiliadoId, Long reservaId, Double valor, String numeroTarjetaSimulado) {
        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Afiliado no encontrado: " + afiliadoId));
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada: " + reservaId));

        Pago pago = new Pago();
        pago.setAfiliado(afiliado);
        pago.setReserva(reserva);
        pago.setConcepto("Pago de reserva #" + reservaId);
        pago.setValor(valor);
        pago.setNumeroTarjetaSimulado(numeroTarjetaSimulado);
        pago.setFechaPago(LocalDateTime.now());

        return procesarPago(pago);
    }

    @Override
    @Transactional
    public Pago pagarMembresia(Long afiliadoId, Long membresiaId, Double valor, String numeroTarjetaSimulado) {
        Afiliado afiliado = afiliadoRepository.findById(afiliadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Afiliado no encontrado: " + afiliadoId));
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Membresía no encontrada: " + membresiaId));

        Pago pago = new Pago();
        pago.setAfiliado(afiliado);
        pago.setMembresia(membresia);
        pago.setConcepto("Pago de membresía #" + membresiaId);
        pago.setValor(valor);
        pago.setNumeroTarjetaSimulado(numeroTarjetaSimulado);
        pago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = procesarPago(pago);

        // Si el pago fue aprobado, activa la membresía asociada
        if ("APROBADO".equals(pagoGuardado.getEstado())) {
            membresiaService.activar(membresiaId);
        }

        return pagoGuardado;
    }

    // Simula el procesamiento del pago contra una pasarela y guarda el resultado
    private Pago procesarPago(Pago pago) {
        pago.setReferenciaPago(UUID.randomUUID().toString());
        pago.setEstado("APROBADO");
        pago.setMensajePasarela("Pago simulado procesado correctamente");

        return pagoRepository.save(pago);
    }

}