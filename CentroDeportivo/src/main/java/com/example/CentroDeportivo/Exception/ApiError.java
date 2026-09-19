package com.example.CentroDeportivo.Exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String mensaje,
        String path,
        List<String> detalles) {
    public ApiError(LocalDateTime timestamp, int status, String mensaje, String path){
        this(timestamp, status, mensaje, path, List.of());
    }
        }









