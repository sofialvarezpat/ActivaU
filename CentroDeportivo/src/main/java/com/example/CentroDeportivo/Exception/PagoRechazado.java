package com.example.CentroDeportivo.Exception;

public class PagoRechazado extends RuntimeException {
    public PagoRechazado(String message) {
        super(message);
    }
}
