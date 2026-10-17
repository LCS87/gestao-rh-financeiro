package br.com.callcenter.modulorh.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.callcenter.modulorh.domain.entity.Beneficio;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.domain.entity.FuncionarioBeneficio;
import br.com.callcenter.modulorh.domain.enums.TipoCobrancaBeneficio;
import br.com.callcenter.modulorh.infrastructure.repository.BeneficioRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioBeneficioRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioBeneficioRepository funcionarioBeneficioRepository;

    // CRUD para Beneficios
    public List<Beneficio> listarBeneficiosAtivos() {
        return beneficioRepository.findByAtivoTrue();
    }

    public List<Beneficio> listarBeneficiosOpcionaisAtivos() {
        return beneficioRepository.findBeneficiosOpcionaisAtivos();
    }

    public List<Beneficio> listarBeneficiosObrigatoriosAtivos() {
        return beneficioRepository.findByObrigatorioTrueAndAtivoTrue();
    }

    public Beneficio buscarBeneficioPorId(UUID id) {
        return beneficioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Benefício não encontrado com ID: " + id));
    }

    @Transactional
    public Beneficio criarBeneficio(Beneficio beneficio) {
        validarBeneficio(beneficio);

        // Verificar se já existe benefício com mesmo nome ativo
        beneficioRepository.findByNomeAndAtivoTrue(beneficio.getNome())
                .ifPresent(b -> {
                    throw new RuntimeException("Já existe um benefício ativo com o nome: " + beneficio.getNome());
                });

        return beneficioRepository.save(beneficio);
    }

    @Transactional
    public Beneficio atualizarBeneficio(UUID id, Beneficio beneficioAtualizado) {
        Beneficio beneficioExistente = buscarBeneficioPorId(id);

        // Verificar se o nome foi alterado e se já existe outro com esse nome
        if (!beneficioExistente.getNome().equals(beneficioAtualizado.getNome())) {
            beneficioRepository.findByNomeAndAtivoTrue(beneficioAtualizado.getNome())
                    .ifPresent(b -> {
                        throw new RuntimeException(
                                "Já existe um benefício ativo com o nome: " + beneficioAtualizado.getNome());
                    });
        }

        // Atualizar campos permitidos
        beneficioExistente.setNome(beneficioAtualizado.getNome());
        beneficioExistente.setDescricao(beneficioAtualizado.getDescricao());
        beneficioExistente.setValor(beneficioAtualizado.getValor());
        beneficioExistente.setPercentualDesconto(beneficioAtualizado.getPercentualDesconto());
        beneficioExistente.setAtivo(beneficioAtualizado.getAtivo());
        beneficioExistente.setObrigatorio(beneficioAtualizado.getObrigatorio());

        validarBeneficio(beneficioExistente);

        return beneficioRepository.save(beneficioExistente);
    }

    @Transactional
    public void desativarBeneficio(UUID id) {
        Beneficio beneficio = buscarBeneficioPorId(id);

        // Verificar se há funcionários associados ativos
        List<FuncionarioBeneficio> associacoesAtivas = funcionarioBeneficioRepository
                .findFuncionariosAtivosPorBeneficio(id);

        if (!associacoesAtivas.isEmpty()) {
            throw new RuntimeException("Não é possível desativar o benefício. Existem " +
                    associacoesAtivas.size() + " funcionário(s) associado(s).");
        }

        beneficio.setAtivo(false);
        beneficioRepository.save(beneficio);
        log.info("Benefício desativado: {}", beneficio.getNome());
    }

    // Gestão de Benefícios por Funcionário
    @Transactional
    public FuncionarioBeneficio adicionarBeneficioAFuncionario(
            UUID funcionarioId, UUID beneficioId,
            LocalDate dataInicio, LocalDate dataFim) {

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + funcionarioId));

        Beneficio beneficio = buscarBeneficioPorId(beneficioId);

        if (!beneficio.getAtivo()) {
            throw new RuntimeException("Benefício não está ativo");
        }

        // Verificar se já existe associação ativa
        funcionarioBeneficioRepository
                .findByFuncionarioIdAndBeneficioIdAndAtivoTrue(funcionarioId, beneficioId)
                .ifPresent(fb -> {
                    throw new RuntimeException("Funcionário já possui este benefício ativo");
                });

        // Calcular valores com base no tipo de cobrança
        BigDecimal valorFuncionario = calcularValorFuncionario(beneficio, funcionario);
        BigDecimal valorEmpresa = calcularValorEmpresa(beneficio, funcionario, valorFuncionario);

        FuncionarioBeneficio funcionarioBeneficio = new FuncionarioBeneficio(
                funcionario, beneficio, dataInicio, valorFuncionario, valorEmpresa);

        funcionarioBeneficio.setDataFim(dataFim);

        return funcionarioBeneficioRepository.save(funcionarioBeneficio);
    }

    public List<FuncionarioBeneficio> listarBeneficiosPorFuncionario(UUID funcionarioId) {
        funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + funcionarioId));

        return funcionarioBeneficioRepository.findByFuncionarioIdAndAtivoTrue(funcionarioId);
    }

    @Transactional
    public void removerBeneficioDeFuncionario(UUID funcionarioId, UUID beneficioId) {
        FuncionarioBeneficio funcionarioBeneficio = funcionarioBeneficioRepository
                .findByFuncionarioIdAndBeneficioIdAndAtivoTrue(funcionarioId, beneficioId)
                .orElseThrow(() -> new RuntimeException("Benefício não encontrado para este funcionário"));

        funcionarioBeneficio.setAtivo(false);
        funcionarioBeneficio.setDataFim(LocalDate.now());
        funcionarioBeneficioRepository.save(funcionarioBeneficio);

        log.info("Benefício removido do funcionário: {} - {}",
                funcionarioId, funcionarioBeneficio.getBeneficio().getNome());
    }

    @Transactional
    public void atualizarDataFimBeneficio(UUID funcionarioId, UUID beneficioId, LocalDate novaDataFim) {
        FuncionarioBeneficio funcionarioBeneficio = funcionarioBeneficioRepository
                .findByFuncionarioIdAndBeneficioIdAndAtivoTrue(funcionarioId, beneficioId)
                .orElseThrow(() -> new RuntimeException("Benefício não encontrado para este funcionário"));

        if (novaDataFim.isBefore(funcionarioBeneficio.getDataInicio())) {
            throw new RuntimeException("Data de fim não pode ser anterior à data de início");
        }

        funcionarioBeneficio.setDataFim(novaDataFim);
        funcionarioBeneficioRepository.save(funcionarioBeneficio);
    }

    // Métodos auxiliares
    private void validarBeneficio(Beneficio beneficio) {
        if (beneficio.getTipoCobranca() == TipoCobrancaBeneficio.VALOR_FIXO && beneficio.getValor() == null) {
            throw new RuntimeException("Benefício com cobrança VALOR_FIXO deve ter valor definido");
        }

        if (beneficio.getTipoCobranca() == TipoCobrancaBeneficio.PERCENTUAL_SALARIO &&
                beneficio.getPercentualDesconto() == null) {
            throw new RuntimeException("Benefício com cobrança PERCENTUAL_SALARIO deve ter percentual definido");
        }

        if (beneficio.getPercentualDesconto() != null &&
                (beneficio.getPercentualDesconto().compareTo(BigDecimal.ZERO) < 0 ||
                        beneficio.getPercentualDesconto().compareTo(new BigDecimal("100")) > 0)) {
            throw new RuntimeException("Percentual de desconto deve estar entre 0 e 100");
        }
    }

    private BigDecimal calcularValorFuncionario(Beneficio beneficio, Funcionario funcionario) {
        return switch (beneficio.getTipoCobranca()) {
            case VALOR_FIXO -> beneficio.getValor();
            case PERCENTUAL_SALARIO ->
                BigDecimal.valueOf(funcionario.getSalarioBase())
                        .multiply(beneficio.getPercentualDesconto())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            case FUNCIONARIO_CUSTEIA_TOTAL -> beneficio.getValor();
            case COMPARTILHADO ->
                beneficio.getValor() != null ? beneficio.getValor().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
            case EMPRESA_CUSTEIA_TOTAL, VALOR_VARIAVEL -> BigDecimal.ZERO;
        };
    }

    private BigDecimal calcularValorEmpresa(Beneficio beneficio, Funcionario funcionario, BigDecimal valorFuncionario) {
        return switch (beneficio.getTipoCobranca()) {
            case EMPRESA_CUSTEIA_TOTAL -> beneficio.getValor();
            case COMPARTILHADO ->
                beneficio.getValor() != null ? beneficio.getValor().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
            case PERCENTUAL_SALARIO, VALOR_FIXO, FUNCIONARIO_CUSTEIA_TOTAL, VALOR_VARIAVEL ->
                beneficio.getValor() != null ? beneficio.getValor().subtract(valorFuncionario) : BigDecimal.ZERO;
        };
    }

    // Métodos de consulta e relatórios
    public Long contarBeneficiosAtivos() {
        return beneficioRepository.countBeneficiosAtivos();
    }

    public Long contarFuncionariosComBeneficio(UUID beneficioId) {
        return (long) funcionarioBeneficioRepository.findFuncionariosAtivosPorBeneficio(beneficioId).size();
    }

    public BigDecimal calcularCustoTotalMensalEmpresa() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);

        BigDecimal total = BigDecimal.ZERO;
        // TODO: implementar consulta otimizada para custo total
        List<FuncionarioBeneficio> beneficiosAtivos = funcionarioBeneficioRepository.findAll()
                .stream()
                .filter(fb -> fb.getAtivo() &&
                        (fb.getDataFim() == null || !fb.getDataFim().isBefore(inicioMes)) &&
                        !fb.getDataInicio().isAfter(hoje))
                .toList();

        for (FuncionarioBeneficio fb : beneficiosAtivos) {
            total = total.add(fb.getValorEmpresa() != null ? fb.getValorEmpresa() : BigDecimal.ZERO);
        }

        return total;
    }

    public Object[] obterResumoBeneficios() {
        List<Object[]> dados = funcionarioBeneficioRepository.findResumoBeneficiosAtivos();
        // TODO: processar dados para retornar resumo consolidado
        return new Object[] { "Resumo de benefícios", dados.size() + " tipos ativos" };
    }
}