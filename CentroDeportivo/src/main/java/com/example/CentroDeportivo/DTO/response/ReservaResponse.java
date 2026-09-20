package com.example.CentroDeportivo.DTO.response;


import com.example.CentroDeportivo.Entity.Enum.EstadoReserva;
import com.example.CentroDeportivo.Entity.Enum.OrigenCupo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaResponse(
        Long id,
        Long afiliadoId,
        String afiliado,
        Long actividadId,
        String disciplina,
        LocalDate fechaActividad,
        LocalTime horaInicio,
        LocalTime horaFin,
        EstadoReserva estado,
        OrigenCupo origenCupo,
        LocalDateTime fechaReserva,
        Long membresiaId,
        PagoResponse pago) {
}
