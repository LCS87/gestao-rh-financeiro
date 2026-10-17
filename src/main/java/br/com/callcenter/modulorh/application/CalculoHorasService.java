package br.com.callcenter.modulorh.application;

import br.com.callcenter.modulorh.domain.entity.RegistroPonto;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Calcula horas trabalhadas e extras conforme CLT.
 * Regra: 44h semanais, excedente = 50% hora extra.
 */
@Service
public class CalculoHorasService {

    private static final double HORAS_SEMANAIS_CLT = 44.0;
    private static final double HORAS_DIARIAS = HORAS_SEMANAIS_CLT / 6.0; // ~7,33h
    private static final double MULTIPLICADOR_EXTRA = 1.5;

    public record ResumoHoras(double horasNormais, double horasExtras, double totalHoras) {}

    public ResumoHoras calcular(List<RegistroPonto> registros) {
        double totalMinutos = 0;
        for (RegistroPonto rp : registros) {
            totalMinutos += calcularMinutosTrabalhados(rp);
        }
        double totalHoras = totalMinutos / 60.0;
        double horasEsperadas = registros.size() * HORAS_DIARIAS;
        double horasNormais = Math.min(totalHoras, horasEsperadas);
        double horasExtras = Math.max(0, totalHoras - horasEsperadas);
        return new ResumoHoras(horasNormais, horasExtras, totalHoras);
    }

    private long calcularMinutosTrabalhados(RegistroPonto rp) {
        LocalTime saida = rp.getHoraSaida() != null ? rp.getHoraSaida() : LocalTime.MIDNIGHT;
        Duration total = Duration.between(rp.getHoraEntrada(), saida);
        if (rp.getHoraIntervaloInicio() != null && rp.getHoraIntervaloFim() != null) {
            Duration intervalo = Duration.between(rp.getHoraIntervaloInicio(), rp.getHoraIntervaloFim());
            total = total.minus(intervalo);
        }
        return Math.max(0, total.toMinutes());
    }
}
