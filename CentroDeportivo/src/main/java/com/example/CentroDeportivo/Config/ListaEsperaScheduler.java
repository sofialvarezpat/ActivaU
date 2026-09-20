package com.example.CentroDeportivo.Config;

import com.example.CentroDeportivo.Service.CupoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Cada minuto expira las invitaciones de lista de espera no aceptadas a tiempo y pasa al siguiente. */
@Slf4j
@Component
@RequiredArgsConstructor
public class ListaEsperaScheduler {

    private final CupoService cupoService;

    @Scheduled(fixedDelayString = "${app.lista-espera.revision-ms:60000}")
    public void expirarInvitaciones() {
        try {
            cupoService.expirarInvitacionesVencidas();
        } catch (Exception e) {
            log.error("Error al expirar invitaciones de lista de espera", e);
        }
    }
}
