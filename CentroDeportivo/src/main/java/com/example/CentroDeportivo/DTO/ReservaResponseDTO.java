package com.example.CentroDeportivo.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaResponseDTO {
    private Long idReserva;
    private LocalDateTime fechaReserva;
    private String estado;
    private String nombreActividad;
    private String escenario;
    private String mensaje; // Ej: "Reserva exitosa" o "Añadido a lista de espera"
}