package br.com.callcenter.modulorh.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.callcenter.modulorh.domain.entity.AvaliacaoDesempenho;
import br.com.callcenter.modulorh.domain.entity.CicloAvaliacao;
import br.com.callcenter.modulorh.domain.entity.FeedbackAvaliacao;
import br.com.callcenter.modulorh.domain.entity.FormularioAvaliacao;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.domain.entity.QuestaoAvaliacao;
import br.com.callcenter.modulorh.domain.entity.RespostaAvaliacao;
import br.com.callcenter.modulorh.domain.enums.StatusAvaliacaoDesempenho;
import br.com.callcenter.modulorh.domain.enums.TipoAvaliacaoDesempenho;
import br.com.callcenter.modulorh.infrastructure.repository.AvaliacaoDesempenhoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.CicloAvaliacaoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import br.com.callcenter.modulorh.infrastructure.repository.QuestaoAvaliacaoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.RespostaAvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvaliacaoDesempenhoService {

    private final AvaliacaoDesempenhoRepository avaliacaoDesempenhoRepository;
    private final RespostaAvaliacaoRepository respostaAvaliacaoRepository;
    private final QuestaoAvaliacaoRepository questaoAvaliacaoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final CicloAvaliacaoRepository cicloAvaliacaoRepository;

    @Transactional(readOnly = true)
    public List<AvaliacaoDesempenho> listarAvaliacoesPorFuncionario(UUID funcionarioId) {
        log.debug("Listando avaliações para funcionário: {}", funcionarioId);

        return funcionarioRepository.findById(funcionarioId)
                .map(funcionario -> avaliacaoDesempenhoRepository.findByFuncionarioAndStatusNot(
                        funcionario, StatusAvaliacaoDesempenho.CANCELADA))
                .orElseThrow(() -> {
                    log.warn("Funcionário não encontrado: {}", funcionarioId);
                    return new IllegalArgumentException("Funcionário não encontrado");
                });
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoDesempenho> listarAvaliacoesPorCiclo(UUID cicloId) {
        log.debug("Listando avaliações do ciclo: {}", cicloId);

        // Verificar se o ciclo existe
        if (!cicloAvaliacaoRepository.existsById(cicloId)) {
            log.warn("Ciclo não encontrado: {}", cicloId);
            throw new IllegalArgumentException("Ciclo não encontrado");
        }

        // Usar o novo método do repository
        return avaliacaoDesempenhoRepository.findByCicloId(cicloId);
    }

    @Transactional
    public AvaliacaoDesempenho criarAutoavaliacao(UUID cicloId, UUID funcionarioId) {
        log.debug("Criando autoavaliação para funcionário {} no ciclo {}", funcionarioId, cicloId);

        return criarAvaliacao(cicloId, funcionarioId, null, TipoAvaliacaoDesempenho.AUTOAVALIACAO);
    }

    @Transactional
    public AvaliacaoDesempenho criarAvaliacaoGestor(UUID cicloId, UUID funcionarioId, UUID avaliadorId) {
        log.debug("Criando avaliação do gestor para funcionário {} por avaliador {} no ciclo {}",
                funcionarioId, avaliadorId, cicloId);

        Funcionario avaliador = funcionarioRepository.findById(avaliadorId)
                .orElseThrow(() -> {
                    log.warn("Avaliador não encontrado: {}", avaliadorId);
                    return new IllegalArgumentException("Avaliador não encontrado");
                });

        return criarAvaliacao(cicloId, funcionarioId, avaliador, TipoAvaliacaoDesempenho.AVALIACAO_GESTOR);
    }

    @Transactional
    public void salvarResposta(UUID avaliacaoId, UUID questaoId, String resposta, BigDecimal nota,
            String justificativa) {
        log.debug("Salvando resposta para avaliação {} e questão {}", avaliacaoId, questaoId);

        AvaliacaoDesempenho avaliacao = avaliacaoDesempenhoRepository.findById(avaliacaoId)
                .orElseThrow(() -> {
                    log.warn("Avaliação não encontrada: {}", avaliacaoId);
                    return new IllegalArgumentException("Avaliação não encontrada");
                });

        if (avaliacao.isConcluida()) {
            throw new IllegalArgumentException("Não é possível alterar respostas de uma avaliação concluída");
        }

        QuestaoAvaliacao questao = questaoAvaliacaoRepository.findById(questaoId)
                .orElseThrow(() -> {
                    log.warn("Questão não encontrada: {}", questaoId);
                    return new IllegalArgumentException("Questão não encontrada");
                });

        // Buscar ou criar resposta
        RespostaAvaliacao respostaExistente = respostaAvaliacaoRepository
                .findByAvaliacaoAndQuestao(avaliacao, questao)
                .orElse(new RespostaAvaliacao());

        respostaExistente.setAvaliacao(avaliacao);
        respostaExistente.setQuestao(questao);
        respostaExistente.setResposta(resposta);
        respostaExistente.setNota(nota);
        respostaExistente.setJustificativa(justificativa);
        respostaExistente.setDataResposta(LocalDateTime.now());

        respostaAvaliacaoRepository.save(respostaExistente);

        // Atualizar percentual de conclusão da avaliação
        atualizarPercentualConclusao(avaliacao);

        log.info("Resposta salva para avaliação {} e questão {}", avaliacaoId, questaoId);
    }

    @Transactional
    public AvaliacaoDesempenho concluirAvaliacao(UUID avaliacaoId) {
        log.debug("Concluindo avaliação: {}", avaliacaoId);

        return avaliacaoDesempenhoRepository.findById(avaliacaoId).map(avaliacao -> {
            if (avaliacao.isConcluida()) {
                throw new IllegalArgumentException("Avaliação já está concluída");
            }

            // Verificar se todas as questões obrigatórias foram respondidas
            List<QuestaoAvaliacao> questoesObrigatorias = questaoAvaliacaoRepository
                    .findQuestoesObrigatoriasByFormulario(obterFormularioDaAvaliacao(avaliacao).getId());

            long respostasObrigatoriasPreenchidas = questoesObrigatorias.stream()
                    .filter(questao -> respostaAvaliacaoRepository
                            .findByAvaliacaoAndQuestao(avaliacao, questao)
                            .map(resposta -> resposta.getResposta() != null && !resposta.getResposta().isEmpty())
                            .orElse(false))
                    .count();

            if (respostasObrigatoriasPreenchidas < questoesObrigatorias.size()) {
                throw new IllegalArgumentException("Não é possível concluir: há questões obrigatórias não respondidas");
            }

            // Calcular nota final
            calcularNotaFinal(avaliacao);

            // Atualizar status
            avaliacao.setStatus(StatusAvaliacaoDesempenho.CONCLUIDA);
            avaliacao.setDataConclusao(LocalDateTime.now());

            AvaliacaoDesempenho avaliacaoConcluida = avaliacaoDesempenhoRepository.save(avaliacao);

            // Criar feedback autom��tico
            criarFeedbackAutomatico(avaliacaoConcluida);

            log.info("Avaliação concluída: {}", avaliacaoId);
            return avaliacaoConcluida;
        }).orElseThrow(() -> {
            log.warn("Avaliação não encontrada: {}", avaliacaoId);
            return new IllegalArgumentException("Avaliação não encontrada");
        });
    }

    @Transactional(readOnly = true)
    public boolean existemAvaliacoesPendentesNoCiclo(UUID cicloId) {
        log.debug("Verificando se existem avaliações pendentes no ciclo: {}", cicloId);

        long count = avaliacaoDesempenhoRepository.countByCicloEStatus(
                cicloId, StatusAvaliacaoDesempenho.PENDENTE);

        return count > 0;
    }

    @Transactional(readOnly = true)
    public Double calcularMediaNotasCiclo(UUID cicloId) {
        log.debug("Calculando média de notas do ciclo: {}", cicloId);

        Double media = avaliacaoDesempenhoRepository.calcularMediaNotasCiclo(cicloId);
        return media != null ? media : 0.0;
    }

    @Transactional(readOnly = true)
    public List<Object[]> calcularMediaNotasPorDepartamento(UUID cicloId) {
        log.debug("Calculando média de notas por departamento do ciclo: {}", cicloId);

        return avaliacaoDesempenhoRepository.calcularMediaNotasPorDepartamento(cicloId);
    }

    // Métodos privados auxiliares
    private AvaliacaoDesempenho criarAvaliacao(UUID cicloId, UUID funcionarioId,
            Funcionario avaliador, TipoAvaliacaoDesempenho tipo) {
        CicloAvaliacao ciclo = cicloAvaliacaoRepository.findById(cicloId)
                .orElseThrow(() -> {
                    log.warn("Ciclo não encontrado: {}", cicloId);
                    return new IllegalArgumentException("Ciclo não encontrado");
                });

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> {
                    log.warn("Funcionário não encontrado: {}", funcionarioId);
                    return new IllegalArgumentException("Funcionário não encontrado");
                });

        // Verificar se já existe avaliação do mesmo tipo
        Optional<AvaliacaoDesempenho> avaliacaoExistente = avaliacaoDesempenhoRepository
                .findByCicloFuncionarioETipo(cicloId, funcionarioId, tipo);

        if (avaliacaoExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma avaliação deste tipo para este funcionário no ciclo");
        }

        // Criar nova avaliação
        AvaliacaoDesempenho avaliacao = new AvaliacaoDesempenho();
        avaliacao.setCiclo(ciclo);
        avaliacao.setFuncionario(funcionario);
        avaliacao.setAvaliador(avaliador);
        avaliacao.setTipo(tipo);
        avaliacao.setStatus(StatusAvaliacaoDesempenho.PENDENTE);
        avaliacao.setDataInicio(LocalDateTime.now());
        avaliacao.setPercentualConclusao(BigDecimal.ZERO);

        AvaliacaoDesempenho avaliacaoSalva = avaliacaoDesempenhoRepository.save(avaliacao);
        log.info("Avaliação criada: {}", avaliacaoSalva.getId());

        return avaliacaoSalva;
    }

    private void atualizarPercentualConclusao(AvaliacaoDesempenho avaliacao) {
        Long totalQuestoes = questaoAvaliacaoRepository.countByFormulario(
                obterFormularioDaAvaliacao(avaliacao).getId());

        if (totalQuestoes == 0) {
            avaliacao.setPercentualConclusao(BigDecimal.ZERO);
            return;
        }

        Long respostasPreenchidas = respostaAvaliacaoRepository
                .countRespostasPreenchidasByAvaliacao(avaliacao.getId());

        BigDecimal percentual = BigDecimal.valueOf(respostasPreenchidas)
                .divide(BigDecimal.valueOf(totalQuestoes), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        avaliacao.setPercentualConclusao(percentual);
        avaliacao.setDataUltimaAtualizacao(LocalDateTime.now());
        avaliacaoDesempenhoRepository.save(avaliacao);
    }

    private void calcularNotaFinal(AvaliacaoDesempenho avaliacao) {
        List<RespostaAvaliacao> respostas = respostaAvaliacaoRepository
                .findByAvaliacaoIdOrderByQuestaoOrdem(avaliacao.getId());

        if (respostas.isEmpty()) {
            avaliacao.setNotaFinal(BigDecimal.ZERO);
            return;
        }

        BigDecimal somaPonderada = BigDecimal.ZERO;
        BigDecimal totalPeso = BigDecimal.ZERO;

        for (RespostaAvaliacao resposta : respostas) {
            if (resposta.getNota() != null && resposta.getQuestao().getPeso() != null) {
                BigDecimal peso = BigDecimal.valueOf(resposta.getQuestao().getPeso());
                BigDecimal notaPonderada = resposta.getNota().multiply(peso);
                somaPonderada = somaPonderada.add(notaPonderada);
                totalPeso = totalPeso.add(peso);
            }
        }

        if (totalPeso.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal notaFinal = somaPonderada.divide(totalPeso, 2, java.math.RoundingMode.HALF_UP);
            avaliacao.setNotaFinal(notaFinal);
        } else {
            avaliacao.setNotaFinal(BigDecimal.ZERO);
        }
    }

    private void criarFeedbackAutomatico(AvaliacaoDesempenho avaliacao) {
        FeedbackAvaliacao feedback = new FeedbackAvaliacao(avaliacao);
        // TODO: Implementar criação automática de feedback
        // Por enquanto, apenas cria o registro básico
    }

    private FormularioAvaliacao obterFormularioDaAvaliacao(AvaliacaoDesempenho avaliacao) {
        // TODO: Implementar lógica para obter o formulário correto baseado no tipo de
        // avaliação
        // Por enquanto, retorna um formulário padrão (será implementado depois)
        return new FormularioAvaliacao("Formulário Padrão", "Formulário de avaliação padrão",
                br.com.callcenter.modulorh.domain.enums.TipoFormularioAvaliacao.AUTOAVALIACAO);
    }
}