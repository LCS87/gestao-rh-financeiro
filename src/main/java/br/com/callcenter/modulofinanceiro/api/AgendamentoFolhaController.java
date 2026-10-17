package br.com.callcenter.modulofinanceiro.api;

import br.com.callcenter.modulofinanceiro.application.AgendamentoFolhaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/financeiro/agendamento-folha")
public class AgendamentoFolhaController {

    private final AgendamentoFolhaService agendamentoFolhaService;

    public AgendamentoFolhaController(AgendamentoFolhaService agendamentoFolhaService) {
        this.agendamentoFolhaService = agendamentoFolhaService;
    }

    @PostMapping("/gerar/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<Map<String, Object>> gerarFolhasParaMes(@PathVariable String anoMes) {
        YearMonth periodo = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        agendamentoFolhaService.gerarFolhasParaMes(periodo);
        
        return ResponseEntity.ok(Map.of(
            "message", "Folhas geradas com sucesso para o período " + anoMes,
            "periodo", anoMes
        ));
    }

    @PostMapping("/processar/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<Map<String, Object>> processarFolhasPendentes(@PathVariable String anoMes) {
        YearMonth periodo = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        agendamentoFolhaService.processarFolhasPendentesDoMes(periodo);
        
        return ResponseEntity.ok(Map.of(
            "message", "Folhas processadas com sucesso para o período " + anoMes,
            "periodo", anoMes
        ));
    }

    @GetMapping("/resumo/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH','SUPERVISOR')")
    public ResponseEntity<AgendamentoFolhaService.ResumoAgendamento> obterResumo(@PathVariable String anoMes) {
        YearMonth periodo = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        return ResponseEntity.ok(agendamentoFolhaService.obterResumoPeriodo(periodo));
    }

    @GetMapping("/resumo/atual")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH','SUPERVISOR')")
    public ResponseEntity<AgendamentoFolhaService.ResumoAgendamento> obterResumoMesAtual() {
        YearMonth mesAtual = YearMonth.now();
        return ResponseEntity.ok(agendamentoFolhaService.obterResumoPeriodo(mesAtual));
    }

    @GetMapping("/resumo/anterior")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH','SUPERVISOR')")
    public ResponseEntity<AgendamentoFolhaService.ResumoAgendamento> obterResumoMesAnterior() {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        return ResponseEntity.ok(agendamentoFolhaService.obterResumoPeriodo(mesAnterior));
    }
}
