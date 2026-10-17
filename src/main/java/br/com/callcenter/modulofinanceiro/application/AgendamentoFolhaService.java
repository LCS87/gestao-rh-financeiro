package br.com.callcenter.modulofinanceiro.application;

import br.com.callcenter.modulofinanceiro.domain.entity.FolhaPagamento;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.FolhaPagamentoRepository;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AgendamentoFolhaService {

    private final FolhaPagamentoRepository folhaPagamentoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FolhaPagamentoService folhaPagamentoService;
    private final CalculoFolhaService calculoFolhaService;

    public AgendamentoFolhaService(FolhaPagamentoRepository folhaPagamentoRepository,
                                   FuncionarioRepository funcionarioRepository,
                                   FolhaPagamentoService folhaPagamentoService,
                                   CalculoFolhaService calculoFolhaService) {
        this.folhaPagamentoRepository = folhaPagamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.folhaPagamentoService = folhaPagamentoService;
        this.calculoFolhaService = calculoFolhaService;
    }

    /**
     * Agenda automático: Gera folhas do mês anterior no dia 1 de cada mês às 02:00
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    @Transactional
    public void gerarFolhasMensais() {
        log.info("Iniciando geração automática de folhas de pagamento");
        
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        String anoMes = mesAnterior.toString();
        
        List<Funcionario> funcionariosAtivos = funcionarioRepository.findByStatus(Funcionario.StatusFuncionario.ATIVO);
        
        int geradas = 0;
        int ignoradas = 0;
        
        for (Funcionario funcionario : funcionariosAtivos) {
            try {
                // Verifica se já existe folha para este funcionário no período
                if (!folhaPagamentoRepository.existsByFuncionarioIdAndAnoMes(funcionario.getId(), anoMes)) {
                    // Verifica se funcionário estava ativo no período
                    if (funcionarioEstavaAtivoNoPeriodo(funcionario, mesAnterior)) {
                        folhaPagamentoService.criarFolhaFuncionario(funcionario.getId(), mesAnterior);
                        geradas++;
                        log.info("Folha gerada para funcionário {} - período {}", funcionario.getNome(), anoMes);
                    } else {
                        ignoradas++;
                        log.debug("Funcionário {} não estava ativo no período {}", funcionario.getNome(), anoMes);
                    }
                } else {
                    ignoradas++;
                    log.debug("Folha já existe para funcionário {} - período {}", funcionario.getNome(), anoMes);
                }
            } catch (Exception e) {
                log.error("Erro ao gerar folha para funcionário {}: {}", funcionario.getNome(), e.getMessage());
            }
        }
        
        log.info("Geração de folhas concluída: {} geradas, {} ignoradas", geradas, ignoradas);
    }

    /**
     * Processamento automático: Processa folhas pendentes no dia 5 de cada mês às 03:00
     */
    @Scheduled(cron = "0 0 3 5 * ?")
    @Transactional
    public void processarFolhasPendentes() {
        log.info("Iniciando processamento automático de folhas pendentes");
        
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        List<FolhaPagamento> folhasPendentes = folhaPagamentoRepository.findByAnoMes(mesAnterior.toString());
        
        int processadas = 0;
        int erros = 0;
        
        for (FolhaPagamento folha : folhasPendentes) {
            if (folha.getStatus() == FolhaPagamento.StatusFolha.PROCESSANDO) {
                try {
                    // Assume 0 dependentes para processamento automático
                    folhaPagamentoService.processarFolha(folha.getId(), 0);
                    processadas++;
                    log.info("Folha processada automaticamente: ID {}", folha.getId());
                } catch (Exception e) {
                    erros++;
                    log.error("Erro ao processar folha ID {}: {}", folha.getId(), e.getMessage());
                }
            }
        }
        
        log.info("Processamento automático concluído: {} processadas, {} erros", processadas, erros);
    }

    /**
     * Alerta de folhas não processadas: Verifica no dia 10 de cada mês às 09:00
     */
    @Scheduled(cron = "0 0 9 10 * ?")
    public void verificarFolhasNaoProcessadas() {
        log.info("Verificando folhas não processadas");
        
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        Long pendentes = folhaPagamentoService.contarProcessandoNoMes(mesAnterior);
        
        if (pendentes > 0) {
            log.warn("ATENÇÃO: Existem {} folhas não processadas do período {}", pendentes, mesAnterior);
            // Aqui poderia enviar notificação/email para o responsável
        } else {
            log.info("Todas as folhas do período {} foram processadas", mesAnterior);
        }
    }

    /**
     * Gera folhas para um mês específico (manual)
     */
    @Transactional
    public void gerarFolhasParaMes(YearMonth anoMes) {
        log.info("Gerando folhas para o período {}", anoMes);
        
        String anoMesStr = anoMes.toString();
        List<Funcionario> funcionariosAtivos = funcionarioRepository.findByStatus(Funcionario.StatusFuncionario.ATIVO);
        
        int geradas = 0;
        
        for (Funcionario funcionario : funcionariosAtivos) {
            try {
                if (!folhaPagamentoRepository.existsByFuncionarioIdAndAnoMes(funcionario.getId(), anoMesStr)) {
                    if (funcionarioEstavaAtivoNoPeriodo(funcionario, anoMes)) {
                        folhaPagamentoService.criarFolhaFuncionario(funcionario.getId(), anoMes);
                        geradas++;
                    }
                }
            } catch (Exception e) {
                log.error("Erro ao gerar folha para funcionário {}: {}", funcionario.getNome(), e.getMessage());
            }
        }
        
        log.info("Geração manual concluída: {} folhas geradas para o período {}", geradas, anoMes);
    }

    /**
     * Processa todas as folhas pendentes de um período
     */
    @Transactional
    public void processarFolhasPendentesDoMes(YearMonth anoMes) {
        log.info("Processando folhas pendentes do período {}", anoMes);
        
        List<FolhaPagamento> folhasPendentes = folhaPagamentoRepository.findByAnoMes(anoMes.toString());
        
        int processadas = 0;
        
        for (FolhaPagamento folha : folhasPendentes) {
            if (folha.getStatus() == FolhaPagamento.StatusFolha.PROCESSANDO) {
                try {
                    folhaPagamentoService.processarFolha(folha.getId(), 0);
                    processadas++;
                } catch (Exception e) {
                    log.error("Erro ao processar folha ID {}: {}", folha.getId(), e.getMessage());
                }
            }
        }
        
        log.info("Processamento manual concluído: {} folhas processadas para o período {}", processadas, anoMes);
    }

    private boolean funcionarioEstavaAtivoNoPeriodo(Funcionario funcionario, YearMonth periodo) {
        if (funcionario.getStatus() == Funcionario.StatusFuncionario.ATIVO) {
            // Se está ativo agora, verifica se foi admitido antes do fim do período
            if (funcionario.getDataAdmissao() != null) {
                LocalDate fimPeriodo = periodo.atEndOfMonth();
                return funcionario.getDataAdmissao().isBefore(fimPeriodo.plusDays(1));
            }
            return true;
        }
        return false;
    }

    public record ResumoAgendamento(
        int totalFuncionarios,
        int folhasGeradas,
        int folhasProcessadas,
        int folhasPendentes,
        YearMonth periodo
    ) {}

    public ResumoAgendamento obterResumoPeriodo(YearMonth periodo) {
        List<Funcionario> funcionariosAtivos = funcionarioRepository.findByStatus(Funcionario.StatusFuncionario.ATIVO);
        List<FolhaPagamento> folhas = folhaPagamentoRepository.findByAnoMes(periodo.toString());
        
        long geradas = folhas.size();
        long processadas = folhas.stream()
                .filter(f -> f.getStatus() == FolhaPagamento.StatusFolha.PROCESSADA || f.getStatus() == FolhaPagamento.StatusFolha.PAGA)
                .count();
        long pendentes = folhas.stream()
                .filter(f -> f.getStatus() == FolhaPagamento.StatusFolha.PROCESSANDO)
                .count();
        
        return new ResumoAgendamento(
            funcionariosAtivos.size(),
            (int) geradas,
            (int) processadas,
            (int) pendentes,
            periodo
        );
    }
}
