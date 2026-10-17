package br.com.callcenter.modulo_comissoes.application;

import br.com.callcenter.modulo_comissoes.domain.entity.*;
import br.com.callcenter.modulo_comissoes.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComissaoService {

    private final RegraComissaoRepository regraComissaoRepository;
    private final MetaRepository metaRepository;
    private final VendaComissaoRepository vendaComissaoRepository;
    private final ComissaoPagaRepository comissaoPagaRepository;
    private final MetaFuncionarioRepository metaFuncionarioRepository;

    // --- Regras de Comissão ---

    @Transactional(readOnly = true)
    public List<RegraComissao> listarRegrasAtivas() {
        log.debug("Listando regras de comissão ativas");
        return regraComissaoRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public RegraComissao buscarRegraPorId(UUID id) {
        log.debug("Buscando regra de comissão por ID: {}", id);
        return regraComissaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regra de comissão não encontrada: " + id));
    }

    @Transactional
    public RegraComissao criarRegra(RegraComissao regra) {
        log.debug("Criando nova regra de comissão: {}", regra.getNome());
        regra.setAtivo(true);
        return regraComissaoRepository.save(regra);
    }

    @Transactional
    public RegraComissao atualizarRegra(UUID id, RegraComissao regraAtualizada) {
        log.debug("Atualizando regra de comissão: {}", id);
        return regraComissaoRepository.findById(id).map(regra -> {
            regra.setNome(regraAtualizada.getNome());
            regra.setDescricao(regraAtualizada.getDescricao());
            regra.setTipoComissao(regraAtualizada.getTipoComissao());
            regra.setPorcentagem(regraAtualizada.getPorcentagem());
            regra.setValorFixo(regraAtualizada.getValorFixo());
            regra.setValorMinimo(regraAtualizada.getValorMinimo());
            regra.setValorMaximo(regraAtualizada.getValorMaximo());
            regra.setAplicarPorDepartamento(regraAtualizada.getAplicarPorDepartamento());
            regra.setDepartamento(regraAtualizada.getDepartamento());
            return regraComissaoRepository.save(regra);
        }).orElseThrow(() -> new IllegalArgumentException("Regra de comissão não encontrada: " + id));
    }

    @Transactional
    public void desativarRegra(UUID id) {
        log.debug("Desativando regra de comissão: {}", id);
        regraComissaoRepository.findById(id).ifPresent(regra -> {
            regra.setAtivo(false);
            regraComissaoRepository.save(regra);
        });
    }

    // --- Cálculo de Comissões ---

    @Transactional
    public VendaComissao calcularComissaoVenda(UUID vendaId, UUID regraComissaoId) {
        log.debug("Calculando comissão para venda {} com regra {}", vendaId, regraComissaoId);

        // Buscar venda e regra
        var venda = new br.com.callcenter.modulorh.domain.entity.Venda();
        // Na prática, você buscaria a venda do repositório de vendas
        // venda = vendaRepository.findById(vendaId).orElseThrow(...);

        var regra = regraComissaoRepository.findById(regraComissaoId)
                .orElseThrow(() -> new IllegalArgumentException("Regra de comissão não encontrada"));

        // Buscar funcionário da venda (vendedor)
        // Funcionario vendedor = venda.getVendedor();

        // Calcular valor da comissão
        BigDecimal valorComissao = regra.calcularComissao(venda.getValor());

        // Criar venda com comissão
        VendaComissao vendaComissao = new VendaComissao();
        vendaComissao.setVenda(venda);
        vendaComissao.setRegraComissao(regra);
        // vendaComissao.setFuncionario(vendedor);
        vendaComissao.setValorVenda(venda.getValor());
        vendaComissao.setValorComissao(valorComissao);
        vendaComissao.setStatusPagamento(StatusPagamento.PENDENTE);

        return vendaComissaoRepository.save(vendaComissao);
    }

    @Transactional
    public void pagarComissao(UUID vendaComissaoId) {
        log.debug("Pagando comissão: {}", vendaComissaoId);

        VendaComissao vendaComissao = vendaComissaoRepository.findById(vendaComissaoId)
                .orElseThrow(() -> new IllegalArgumentException("Venda com comissão não encontrada"));

        if (vendaComissao.isPaga()) {
            throw new IllegalArgumentException("Comissão já está paga");
        }

        // Pagar comissão
        vendaComissao.pagarComissao();
        vendaComissaoRepository.save(vendaComissao);

        // Criar registro de comissão paga
        String mesReferencia = LocalDate.now().toString().substring(0, 7);
        ComissaoPaga comissaoPaga = new ComissaoPaga(vendaComissao, mesReferencia, LocalDateTime.now());
        comissaoPagaRepository.save(comissaoPaga);

        log.info("Comissão paga com sucesso: {}", vendaComissaoId);
    }

    // --- Metas ---

    @Transactional(readOnly = true)
    public List<Meta> listarMetasFuncionario(UUID funcionarioId) {
        log.debug("Listando metas do funcionário: {}", funcionarioId);
        return metaRepository.findByFuncionarioId(funcionarioId);
    }

    @Transactional
    public Meta criarMeta(Meta meta) {
        log.debug("Criando nova meta para funcionário {}", meta.getFuncionario().getId());
        meta.setValorAlcancado(BigDecimal.ZERO);
        return metaRepository.save(meta);
    }

    @Transactional
    public Meta atualizarValorMeta(UUID metaId, BigDecimal valorAlcancado) {
        log.debug("Atualizando valor da meta {}: {}", metaId, valorAlcancado);

        Meta meta = metaRepository.findById(metaId)
                .orElseThrow(() -> new IllegalArgumentException("Meta não encontrada"));

        meta.atualizarValorAlcancado(valorAlcancado);
        return metaRepository.save(meta);
    }

    @Transactional
    public Meta concluirMeta(UUID metaId) {
        log.debug("Concluindo meta: {}", metaId);

        Meta meta = metaRepository.findById(metaId)
                .orElseThrow(() -> new IllegalArgumentException("Meta não encontrada"));

        meta.setStatus(br.com.callcenter.modulo_comissoes.domain.entity.StatusMeta.CONCLUIDA);
        meta.setDataConclusao(LocalDateTime.now());
        return metaRepository.save(meta);
    }

    // --- Ranking de Vendedores ---

    @Transactional(readOnly = true)
    public List<RankingVendedor> getRankingVendedores(int dias) {
        log.debug("Calculando ranking de vendedores (últimos {} dias)", dias);

        LocalDate inicio = LocalDate.now().minusDays(dias);
        LocalDate fim = LocalDate.now();

        // Buscar todas as comissões pagas no período
        List<VendaComissao> vendasComissao = vendaComissaoRepository.findAll();

        // Calcular totais por vendedor (simplificado)
        // Em produção, filtrar por data e agrupar por vendedor

        return vendasComissao.stream()
                .map(v -> new RankingVendedor(
                        v.getFuncionario().getId(),
                        v.getFuncionario().getNome(),
                        v.getValorVenda(),
                        v.getValorComissao()
                ))
                .toList();
    }

    // DTO para ranking
    public static class RankingVendedor {
        private UUID funcionarioId;
        private String nome;
        private BigDecimal totalVendas;
        private BigDecimal totalComissoes;

        public RankingVendedor(UUID funcionarioId, String nome, BigDecimal totalVendas, BigDecimal totalComissoes) {
            this.funcionarioId = funcionarioId;
            this.nome = nome;
            this.totalVendas = totalVendas;
            this.totalComissoes = totalComissoes;
        }

        public UUID getFuncionarioId() { return funcionarioId; }
        public void setFuncionarioId(UUID funcionarioId) { this.funcionarioId = funcionarioId; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public BigDecimal getTotalVendas() { return totalVendas; }
        public void setTotalVendas(BigDecimal totalVendas) { this.totalVendas = totalVendas; }
        public BigDecimal getTotalComissoes() { return totalComissoes; }
        public void setTotalComissoes(BigDecimal totalComissoes) { this.totalComissoes = totalComissoes; }
    }
}
