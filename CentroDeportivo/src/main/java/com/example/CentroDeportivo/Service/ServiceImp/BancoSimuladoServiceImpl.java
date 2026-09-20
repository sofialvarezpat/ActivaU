package com.example.CentroDeportivo.Service.ServiceImp;


import com.example.CentroDeportivo.DTO.request.BancoRequest;
import com.example.CentroDeportivo.DTO.response.RespuestaBanco;
import com.example.CentroDeportivo.Entity.Enum.ResultadoBanco;
import com.example.CentroDeportivo.Service.BancoSimuladoService;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.YearMonth;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Banco de mentira, determinista para poder probarlo:
 * formato inválido / vencida => RECHAZADA; termina en 0000 => fondos insuficientes;
 * termina en 9999 => ERROR de conexión simulado; cualquier otra tarjeta válida => APROBADA.
 */
@Service
public class BancoSimuladoServiceImpl implements BancoSimuladoService {

    private static final Pattern EXPIRACION = Pattern.compile("^(0[1-9]|1[0-2])/(\\d{2})$");

    private final Clock clock;

    public BancoSimuladoServiceImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public RespuestaBanco procesar(BancoRequest req) {
        String numero = req.numeroTarjeta() == null ? "" : req.numeroTarjeta().replaceAll("[\\s-]", "");
        if (!numero.matches("\\d{13,19}")) {
            return rechazo("Formato de tarjeta inválido");
        }
        if (req.cvv() == null || !req.cvv().matches("\\d{3,4}")) {
            return rechazo("CVV inválido");
        }
        Matcher m = EXPIRACION.matcher(req.fechaExpiracion() == null ? "" : req.fechaExpiracion().trim());
        if (!m.matches()) {
            return rechazo("Fecha de expiración inválida (use MM/yy)");
        }
        YearMonth expira = YearMonth.of(2000 + Integer.parseInt(m.group(2)), Integer.parseInt(m.group(1)));
        if (expira.isBefore(YearMonth.now(clock))) {
            return rechazo("Tarjeta vencida");
        }
        if (numero.endsWith("9999")) {
            return new RespuestaBanco(ResultadoBanco.ERROR, null, "Error simulado de conexión con el banco");
        }
        if (numero.endsWith("0000")) {
            return rechazo("Fondos insuficientes");
        }
        String referencia = "SIM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return new RespuestaBanco(ResultadoBanco.APROBADA, referencia, "Transacción Aprobada");
    }

    private RespuestaBanco rechazo(String motivo) {
        return new RespuestaBanco(ResultadoBanco.RECHAZADA, null, "Transacción Rechazada: " + motivo);
    }
}
