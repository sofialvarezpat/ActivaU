package com.example.CentroDeportivo.Exception;

/** Recurso no encontrado (404) */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
