package com.example.CentroDeportivo.DTO.request;

/** Constantes de validación compartidas por varios DTOs. */
public final class Patrones {

    private Patrones() {
    }

    public static final String CONTRASENA = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$";
    public static final String CONTRASENA_MSG = "Debe tener entre 8 y 72 caracteres, con mayúscula, minúscula y número";
}
