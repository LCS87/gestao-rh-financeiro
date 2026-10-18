package br.com.callcenter.modulofinanceiro.application;

import br.com.callcenter.core.exception.ResourceNotFoundException;
import br.com.callcenter.modulo_comissoes.api.dto.ApuracaoComissaoResponse;
import br.com.callcenter.modulo_comissoes.application.ComissaoService;
import br.com.callcenter.modulofinanceiro.domain.entity.FolhaPagamento;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.FolhaPagamentoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class FolhaPagamentoService {

    private final FolhaPagamentoRepository folhaPagamentoRepository;
    private final CalculoFolhaService calculoFolhaService;
    private final FuncionarioRepository funcionarioRepository;
    private final ComissaoService comissaoService;

    public FolhaPagamentoService(FolhaPagamentoRepository folhaPagamentoRepository,
                                 CalculoFolhaService calculoFolhaService,
                                 FuncionarioRepository funcionarioRepository,
                                 ComissaoService comissaoService) {
        this.folhaPagamentoRepository = folhaPagamentoRepository;
        this.calculoFolhaService = calculoFolhaService;
        this.funcionarioRepository = funcionarioRepository;
        this.comissaoService = comissaoService;
    }

    public List<FolhaPagamento> listarTodas() {
        return folhaPagamentoRepository.findAll();
    }

    public FolhaPagamento buscarPorId(UUID id) {
        return folhaPagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folha de pagamento não encontrada"));
    }

    public List<FolhaPagamento> listarPorFuncionario(UUID funcionarioId) {
        return folhaPagamentoRepository.findByFuncionarioId(funcionarioId);
    }

    public List<FolhaPagamento> listarPorMes(YearMonth anoMes) {
        return folhaPagamentoRepository.findByAnoMes(anoMes.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    public List<FolhaPagamento> listarPorStatus(FolhaPagamento.StatusFolha status) {
        return folhaPagamentoRepository.findByStatus(status);
    }

    public FolhaPagamento buscarPorFuncionarioEMes(UUID funcionarioId, YearMonth anoMes) {
        return folhaPagamentoRepository.findByFuncionarioEAnoMes(funcionarioId, anoMes.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    public FolhaPagamento criarFolhaFuncionario(UUID funcionarioId, YearMonth anoMes) {
        String anoMesStr = anoMes.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        if (folhaPagamentoRepository.existsByFuncionarioIdAndAnoMes(funcionarioId, anoMesStr)) {
            throw new RuntimeException("Folha de pagamento já existe para este funcionário no período");
        }

        FolhaPagamento folha = calculoFolhaService.calcularFolha(funcionarioId, anoMes);
        folha.setStatus(FolhaPagamento.StatusFolha.PROCESSANDO);
        
        return folhaPagamentoRepository.save(folha);
    }

    public void gerarFolhaEmLote(YearMonth anoMes) {
        String anoMesStr = anoMes.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // Pega todos os funcionários ativos (se a entidade não tiver flag status, vamos pegar todos)
        // Aqui inferimos que há uma tabela de Fucionario via repositório.
        funcionarioRepository.findAll().forEach(f -> {
            if (!folhaPagamentoRepository.existsByFuncionarioIdAndAnoMes(f.getId(), anoMesStr)) {
                try {
                    criarFolhaFuncionario(f.getId(), anoMes);
                } catch (Exception e) {
                    log.error("Erro ao gerar folha para {}", f.getId(), e);
                }
            }
        });
    }

    public FolhaPagamento processarFolha(UUID id, Integer numeroDependentes) {
        FolhaPagamento folha = buscarPorId(id);
        
        if (folha.getStatus() != FolhaPagamento.StatusFolha.PROCESSANDO) {
            throw new RuntimeException("Folha já foi processada");
        }

        // Calcula INSS
        calculoFolhaService.calcularINSS(folha);
        
        // Calcula IRRF
        calculoFolhaService.calcularIRRF(folha, numeroDependentes);
        
        // Calcula FGTS
        calculoFolhaService.calcularFGTS(folha);

        folha.setStatus(FolhaPagamento.StatusFolha.PROCESSADA);
        folha.setDataProcessamento(LocalDate.now());
        
        return folhaPagamentoRepository.save(folha);
    }

    public FolhaPagamento adicionarProvento(UUID id, String tipoProvento, BigDecimal valor) {
        FolhaPagamento folha = buscarPorId(id);
        
        if (folha.getStatus() == FolhaPagamento.StatusFolha.PAGA) {
            throw new RuntimeException("Folha já foi paga");
        }

        switch (tipoProvento.toLowerCase()) {
            case "horas_extras":
                folha.setHorasExtras((folha.getHorasExtras() != null ? folha.getHorasExtras() : BigDecimal.ZERO).add(valor));
                break;
            case "adicional_noturno":
                folha.setAdicionalNoturno((folha.getAdicionalNoturno() != null ? folha.getAdicionalNoturno() : BigDecimal.ZERO).add(valor));
                break;
            case "comissoes":
                folha.setComissoes((folha.getComissoes() != null ? folha.getComissoes() : BigDecimal.ZERO).add(valor));
                break;
            case "bonus":
                folha.setBonus((folha.getBonus() != null ? folha.getBonus() : BigDecimal.ZERO).add(valor));
                break;
            case "ferias":
                folha.setFerias((folha.getFerias() != null ? folha.getFerias() : BigDecimal.ZERO).add(valor));
                break;
            case "decimo_terceiro":
                folha.setDecimoTerceiro((folha.getDecimoTerceiro() != null ? folha.getDecimoTerceiro() : BigDecimal.ZERO).add(valor));
                break;
            case "outros_proventos":
                folha.setOutrosProventos((folha.getOutrosProventos() != null ? folha.getOutrosProventos() : BigDecimal.ZERO).add(valor));
                break;
            default:
                throw new RuntimeException("Tipo de provento inválido: " + tipoProvento);
        }
        
        return folhaPagamentoRepository.save(folha);
    }

    public FolhaPagamento adicionarDesconto(UUID id, String tipoDesconto, BigDecimal valor) {
        FolhaPagamento folha = buscarPorId(id);
        
        if (folha.getStatus() == FolhaPagamento.StatusFolha.PAGA) {
            throw new RuntimeException("Folha já foi paga");
        }

        switch (tipoDesconto.toLowerCase()) {
            case "vale_transporte":
                folha.setValeTransporte((folha.getValeTransporte() != null ? folha.getValeTransporte() : BigDecimal.ZERO).add(valor));
                break;
            case "vale_refeicao":
                folha.setValeRefeicao((folha.getValeRefeicao() != null ? folha.getValeRefeicao() : BigDecimal.ZERO).add(valor));
                break;
            case "plano_saude":
                folha.setPlanoSaude((folha.getPlanoSaude() != null ? folha.getPlanoSaude() : BigDecimal.ZERO).add(valor));
                break;
            case "outros_descontos":
                folha.setOutrosDescontos((folha.getOutrosDescontos() != null ? folha.getOutrosDescontos() : BigDecimal.ZERO).add(valor));
                break;
            default:
                throw new RuntimeException("Tipo de desconto inválido: " + tipoDesconto);
        }
        
        return folhaPagamentoRepository.save(folha);
    }

    public FolhaPagamento pagarFolha(UUID id, String banco, String agencia, String conta) {
        FolhaPagamento folha = buscarPorId(id);
        
        if (folha.getStatus() != FolhaPagamento.StatusFolha.PROCESSADA) {
            throw new RuntimeException("Folha precisa estar processada para ser paga");
        }

        folha.setStatus(FolhaPagamento.StatusFolha.PAGA);
        folha.setDataPagamento(LocalDate.now());
        folha.setBancoPagamento(banco);
        folha.setAgenciaPagamento(agencia);
        folha.setContaPagamento(conta);
        
        return folhaPagamentoRepository.save(folha);
    }

    public BigDecimal calcularTotalFolha(YearMonth anoMes) {
        Double total = folhaPagamentoRepository.sumTotalPagoNoMes(anoMes.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    /**
     * Integração automática da apuração de comissões na Folha de Pagamento (Fase 4).
     * Consolida o total de comissões apurado no mês (via ComissaoService.apurarComissoesMes)
     * no campo "comissoes" da folha de cada funcionário, apenas para folhas ainda não pagas.
     * O valor apurado substitui a comissão anterior (reapuração idempotente) e os totais
     * (proventos/descontos/salário líquido) são recalculados pela própria entidade.
     */
    public int aplicarComissoesNaFolha(String mesReferencia) {
        ApuracaoComissaoResponse apuracao = comissaoService.apurarComissoesMes(mesReferencia);
        int aplicadas = 0;

        for (ApuracaoComissaoResponse.ApuracaoPorFuncionario item : apuracao.getFuncionarios()) {
            FolhaPagamento folha = folhaPagamentoRepository.findByFuncionarioEAnoMes(
                    item.getFuncionarioId(), mesReferencia);

            if (folha == null) {
                log.warn("Folha {} não encontrada para o funcionário {} — comissão de R$ {} não aplicada",
                        mesReferencia, item.getFuncionarioId(), item.getTotalComissoes());
                continue;
            }
            if (folha.getStatus() == FolhaPagamento.StatusFolha.PAGA) {
                log.info("Folha {} do funcionário {} já está paga — comissão não reaplicada",
                        mesReferencia, item.getFuncionarioId());
                continue;
            }

            folha.setComissoes(item.getTotalComissoes());
            folha.recalcularTotais();
            folhaPagamentoRepository.save(folha);
            aplicadas++;
        }

        log.info("Apuração de comissões {}: {} folhas atualizadas, total R$ {}",
                mesReferencia, aplicadas, apuracao.getTotalGeralComissoes());
        return aplicadas;
    }

    public Long contarProcessandoNoMes(YearMonth anoMes) {
        return folhaPagamentoRepository.countProcessandoNoMes(anoMes.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    public void deletar(UUID id) {
        FolhaPagamento existente = buscarPorId(id);
        folhaPagamentoRepository.delete(existente);
    }
}
