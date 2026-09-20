package com.example.CentroDeportivo.Exception;

/** Violación de una regla de negocio (400) */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String mensaje) {
        super(mensaje);
    }
}
