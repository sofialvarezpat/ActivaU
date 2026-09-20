package com.example.CentroDeportivo.Exception;

/** La pasarela de pago rechazó la transacción (402). */
public class PagoRechazadoException extends RuntimeException {

    public PagoRechazadoException(String mensaje) {
        super(mensaje);
    }
}
