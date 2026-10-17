package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.RegistroPontoDto;
import br.com.callcenter.modulorh.application.CalculoHorasService;
import br.com.callcenter.modulorh.application.RegistroPontoService;
import br.com.callcenter.modulorh.domain.entity.RegistroPonto;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rh/ponto")
public class RegistroPontoController {

    private final RegistroPontoService registroPontoService;
    private final CalculoHorasService calculoHorasService;

    public RegistroPontoController(RegistroPontoService registroPontoService,
                                   CalculoHorasService calculoHorasService) {
        this.registroPontoService = registroPontoService;
        this.calculoHorasService = calculoHorasService;
    }

    @GetMapping("/funcionario/{funcionarioId}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<RegistroPonto>> listarPorFuncionarioEPeriodo(
            @PathVariable UUID funcionarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(registroPontoService.listarPorFuncionarioEPeriodo(funcionarioId, inicio, fim));
    }

    @GetMapping("/funcionario/{funcionarioId}/calcular-horas")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Map<String, Double>> calcularHoras(
            @PathVariable UUID funcionarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        List<RegistroPonto> registros = registroPontoService.listarPorFuncionarioEPeriodo(funcionarioId, inicio, fim);
        var resumo = calculoHorasService.calcular(registros);
        return ResponseEntity.ok(Map.of(
                "horasNormais", resumo.horasNormais(),
                "horasExtras", resumo.horasExtras(),
                "totalHoras", resumo.totalHoras()
        ));
    }

    @PostMapping("/entrada")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<RegistroPonto> registrarEntrada(
            @RequestParam UUID funcionarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(registroPontoService.registrarEntrada(funcionarioId, data));
    }

    @PostMapping("/saida")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<RegistroPonto> registrarSaida(
            @RequestParam UUID funcionarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(registroPontoService.registrarSaida(funcionarioId, data));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<RegistroPonto> salvar(@Valid @RequestBody RegistroPontoDto dto) {
        return ResponseEntity.ok(registroPontoService.salvarCompleto(dto));
    }
}
