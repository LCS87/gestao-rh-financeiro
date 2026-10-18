package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.PausaNR17Request;
import br.com.callcenter.modulorh.api.dto.RegistroPontoDto;
import br.com.callcenter.modulorh.application.AfdService;
import br.com.callcenter.modulorh.application.CalculoHorasService;
import br.com.callcenter.modulorh.application.RegistroPontoService;
import br.com.callcenter.modulorh.domain.entity.RegistroPonto;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
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
    private final AfdService afdService;

    public RegistroPontoController(RegistroPontoService registroPontoService,
                                   CalculoHorasService calculoHorasService,
                                   AfdService afdService) {
        this.registroPontoService = registroPontoService;
        this.calculoHorasService = calculoHorasService;
        this.afdService = afdService;
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

    @PostMapping("/intervalo")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<RegistroPonto> registrarIntervalo(
            @RequestParam UUID funcionarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "true") boolean inicioIntervalo) {
        return ResponseEntity.ok(registroPontoService.registrarIntervalo(funcionarioId, data, inicioIntervalo));
    }

    /**
     * Pausas obrigatórias NR-17 (teleatendimento): pausa-descanso 10 min
     * e pausa-lanche 20 min. Lanca como intervalo no registro do dia e
     * registra a justificativa nas observacoes para auditoria.
     */
    @PostMapping("/pausa-nr17")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RegistroPonto> registrarPausaNR17(@Valid @RequestBody PausaNR17Request request) {
        LocalDate hoje = LocalDate.now();
        boolean descanso = request.tipo() == PausaNR17Request.TipoPausa.DESCANSO_10MIN;
        RegistroPonto rp = registroPontoService.buscarOpcionalPorFuncionarioEData(request.funcionarioId(), hoje);
        if (rp == null) {
            rp = registroPontoService.registrarEntrada(request.funcionarioId(), hoje);
        }
        String tag = (descanso ? "[NR-17 pausa-descanso 10min]" : "[NR-17 pausa-lanche 20min]")
                + (request.observacoes() != null ? " " + request.observacoes() : "");
        rp.setObservacoes(rp.getObservacoes() == null ? tag : rp.getObservacoes() + " | " + tag);
        return ResponseEntity.ok(registroPontoService.salvarCompletoParaAjuste(rp));
    }

    /**
     * Espelho detalhado com segregacao de horas extras 50% (dias uteis)
     * e 100% (domingos/feriados), Portaria 671 / CLT.
     */
    @GetMapping("/funcionario/{funcionarioId}/calcular-horas-detalhado")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Map<String, Double>> calcularHorasDetalhado(
            @PathVariable UUID funcionarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        List<RegistroPonto> registros = registroPontoService.listarPorFuncionarioEPeriodo(funcionarioId, inicio, fim);
        var resumo = calculoHorasService.calcularDetalhado(registros);
        return ResponseEntity.ok(Map.of(
                "horasNormais", resumo.horasNormais(),
                "horasExtras50", resumo.horasExtras50(),
                "horasExtras100", resumo.horasExtras100(),
                "totalHoras", resumo.totalHoras()
        ));
    }

    /**
     * Exportacao AFD/AFDT (Portaria 671 MTE - formato texto) para
     * homologacao com relógios de ponto fisicos ou SREP.
     */
    @GetMapping("/funcionario/{funcionarioId}/afd")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<String> exportarAfd(
            @PathVariable UUID funcionarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) String cnpjEmpresa) {
        List<RegistroPonto> registros = registroPontoService.listarPorFuncionarioEPeriodo(funcionarioId, inicio, fim);
        String nome = registroPontoService.nomeFuncionario(funcionarioId);
        String afd = afdService.gerarAfd(registros, nome, funcionarioId.toString(), cnpjEmpresa);
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"afd_" + funcionarioId + "_" + inicio + ".txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(afd);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<RegistroPonto> salvar(@Valid @RequestBody RegistroPontoDto dto) {
        return ResponseEntity.ok(registroPontoService.salvarCompleto(dto));
    }
}
