package com.example.CentroDeportivo.Service;

import com.example.CentroDeportivo.Entity.Reprogramacion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReprogramacionService {

    //Busqueda normal
    List<Reprogramacion> listarPorActividad(Long actividadId);

    //Se agrego a la entidad los campos "fechaNueva", "horaInicioAnterior",
    //"horaFinAnterior", "horaInicioNueva", "horaFinNueva" (renombrando horaAnterior/horaNueva)
    //Se deja la firma del metodo completa porque es lo que exige el requerimiento; la implementación
    //no podrá persistir todo correctamente hasta que se actualice la entidad
    //Guarda el estado anterior de la actividad como historial y aplica la nueva
    //fecha y hora, validando que no se generen traslapes en el nuevo horario
    Reprogramacion reprogramar(Long actividadId, LocalDate nuevaFecha,
                               LocalTime nuevaHoraInicio, LocalTime nuevaHoraFin, String motivo);

    Reprogramacion obtenerPorId(Long id);
}