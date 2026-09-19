package com.example.CentroDeportivo.Service.ServiceImp;

import com.example.CentroDeportivo.Entity.Afiliado;
import com.example.CentroDeportivo.Entity.Enum.EstadoPago;
import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;
import com.example.CentroDeportivo.Entity.Membresia;
import com.example.CentroDeportivo.Entity.Pago;
import com.example.CentroDeportivo.Entity.Reserva;
import com.example.CentroDeportivo.Exception.ConflictException;
import com.example.CentroDeportivo.Exception.RecursoNoEncontradoException;
import com.example.CentroDeportivo.Repository.AfiliadoRepository;
import com.example.CentroDeportivo.Repository.MembresiaRepository;
import com.example.CentroDeportivo.Repository.PagoRepository;
import com.example.CentroDeportivo.Repository.ReservaRepository;
import com.example.CentroDeportivo.Service.MembresiaService;
import com.example.CentroDeportivo.Service.PagoService;
import com.example.CentroDeportivo.Service.ReservaService;
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
    private final ReservaService reservaService;

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

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ConflictException(
                    "Solo se pueden pagar reservas pendientes (estado actual: " + reserva.getEstado() + ")");
        }

        Pago pago = new Pago();
        pago.setAfiliado(afiliado);
        pago.setReserva(reserva);
        pago.setConcepto("Pago de reserva #" + reservaId);
        pago.setValor(valor);
        pago.setNumeroTarjetaSimulado(numeroTarjetaSimulado);
        pago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = procesarPago(pago);

        //  solo si la pasarela aprueba, la reserva pasa a CONFIRMADA.
        if (pagoGuardado.getEstado() == EstadoPago.APROBADO) {
            reservaService.confirmarPorPago(reservaId);
        }

        return pagoGuardado;
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
        if (pagoGuardado.getEstado() == EstadoPago.APROBADO) {
            membresiaService.activar(membresiaId);
        }

        return pagoGuardado;
    }

    // Simula el procesamiento del pago contra una pasarela
    // aprueba si el último dígito de la tarjeta es impar, rechaza si es par
    // o si el número no tiene un formato válido.
    private Pago procesarPago(Pago pago) {
        EstadoPago resultado = evaluarPasarela(pago.getNumeroTarjetaSimulado());

        pago.setReferenciaPasarela(UUID.randomUUID().toString());
        pago.setEstado(resultado);
        pago.setMensajePasarela(resultado == EstadoPago.APROBADO
                ? "Transacción aprobada"
                : "Transacción rechazada (simulado)");

        return pagoRepository.save(pago);
    }

    private EstadoPago evaluarPasarela(String numeroTarjetaSimulado) {
        if (numeroTarjetaSimulado == null || numeroTarjetaSimulado.isBlank()) {
            return EstadoPago.RECHAZADO;
        }

        char ultimoCaracter = numeroTarjetaSimulado.trim().charAt(numeroTarjetaSimulado.trim().length() - 1);
        int ultimoDigito = Character.digit(ultimoCaracter, 10);

        if (ultimoDigito < 0) {
            return EstadoPago.RECHAZADO; // formato inválido
        }

        return (ultimoDigito % 2 != 0) ? EstadoPago.APROBADO : EstadoPago.RECHAZADO;
    }
}