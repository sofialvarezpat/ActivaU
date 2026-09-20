package com.example.CentroDeportivo.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Reloj único de la aplicación. Toda comparación de fechas (12 h de cancelación, inicio de clase,
 * vigencia de membresía) usa este reloj, así se evitan errores de zona horaria (riesgo de RF11)
 * y las pruebas pueden fijar la hora.
 */
@Configuration
@EnableScheduling
public class TiempoConfig {

    @Bean
    public Clock clock(@Value("${app.timezone:America/Bogota}") String zona) {
        return Clock.system(ZoneId.of(zona));
    }
}
