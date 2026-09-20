package com.example.CentroDeportivo.DTO.response;

public record AuthResponse(String token, String tipo, long expiraEnMs, String rol) {
}
