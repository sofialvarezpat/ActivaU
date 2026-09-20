package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.ResultadoBanco;

/** Respuesta de la pasarela simulada: APROBADA (con referencia), RECHAZADA o ERROR de conexión. */
public record RespuestaBanco(ResultadoBanco resultado, String referencia, String mensaje) {
}
