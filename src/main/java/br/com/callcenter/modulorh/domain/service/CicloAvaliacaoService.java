package br.com.callcenter.modulorh.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.callcenter.modulorh.domain.entity.CicloAvaliacao;
import br.com.callcenter.modulorh.domain.enums.StatusCicloAvaliacao;
import br.com.callcenter.modulorh.infrastructure.repository.CicloAvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CicloAvaliacaoService {

    private final CicloAvaliacaoRepository cicloAvaliacaoRepository;
    // TODO: Descomentar quando AvaliacaoDesempenhoService for criado
    // private final AvaliacaoDesempenhoService avaliacaoDesempenhoService;

    @Transactional(readOnly = true)
    public List<CicloAvaliacao> listarCiclosAtivos() {
        log.debug("Listando ciclos de avaliação ativos");
        return cicloAvaliacaoRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<CicloAvaliacao> buscarPorId(UUID id) {
        log.debug("Buscando ciclo de avaliação por ID: {}", id);
        return cicloAvaliacaoRepository.findById(id);
    }

    @Transactional
    public CicloAvaliacao criarCiclo(CicloAvaliacao ciclo) {
        log.debug("Criando novo ciclo de avaliação: {}", ciclo.getNome());

        // Validar se já existe ciclo com mesmo nome, ano e tipo
        boolean jaExiste = cicloAvaliacaoRepository.existsByNomeAndAnoReferenciaAndTipo(
                ciclo.getNome(), ciclo.getAnoReferencia(), ciclo.getTipo());

        if (jaExiste) {
            throw new IllegalArgumentException("Já existe um ciclo com esse nome para o ano e tipo especificados");
        }

        // Validar datas
        validarDatasCiclo(ciclo);

        // Configurar datas automáticas se não informadas
        configurarDatasAutomaticas(ciclo);

        CicloAvaliacao cicloSalvo = cicloAvaliacaoRepository.save(ciclo);
        log.info("Ciclo de avaliação criado com sucesso: {}", cicloSalvo.getId());

        return cicloSalvo;
    }

    @Transactional
    public CicloAvaliacao atualizarCiclo(UUID id, CicloAvaliacao cicloAtualizado) {
        log.debug("Atualizando ciclo de avaliação: {}", id);

        return cicloAvaliacaoRepository.findById(id).map(ciclo -> {
            // Não permitir alterar ciclo concluído ou cancelado
            if (ciclo.getStatus() == StatusCicloAvaliacao.CONCLUIDO ||
                    ciclo.getStatus() == StatusCicloAvaliacao.CANCELADO) {
                throw new IllegalArgumentException("Não é possível alterar um ciclo já concluído ou cancelado");
            }

            // Atualizar campos permitidos
            ciclo.setNome(cicloAtualizado.getNome());
            ciclo.setDescricao(cicloAtualizado.getDescricao());

            if (cicloAtualizado.getDataInicio() != null &&
                    cicloAtualizado.getDataFim() != null) {
                validarDatasCiclo(cicloAtualizado);
                ciclo.setDataInicio(cicloAtualizado.getDataInicio());
                ciclo.setDataFim(cicloAtualizado.getDataFim());
            }

            ciclo.setDataInicioAutoavaliacao(cicloAtualizado.getDataInicioAutoavaliacao());
            ciclo.setDataFimAutoavaliacao(cicloAtualizado.getDataFimAutoavaliacao());
            ciclo.setDataInicioAvaliacaoGestor(cicloAtualizado.getDataInicioAvaliacaoGestor());
            ciclo.setDataFimAvaliacaoGestor(cicloAtualizado.getDataFimAvaliacaoGestor());
            ciclo.setDataInicioFeedback(cicloAtualizado.getDataInicioFeedback());
            ciclo.setDataFimFeedback(cicloAtualizado.getDataFimFeedback());

            // Atualizar formulários
            if (cicloAtualizado.getFormularioAutoavaliacao() != null) {
                ciclo.setFormularioAutoavaliacao(cicloAtualizado.getFormularioAutoavaliacao());
            }

            if (cicloAtualizado.getFormularioAvaliacaoGestor() != null) {
                ciclo.setFormularioAvaliacaoGestor(cicloAtualizado.getFormularioAvaliacaoGestor());
            }

            // Atualizar status se necessário
            atualizarStatusCiclo(ciclo);

            CicloAvaliacao cicloAtualizadoSalvo = cicloAvaliacaoRepository.save(ciclo);
            log.info("Ciclo de avaliação atualizado: {}", id);

            return cicloAtualizadoSalvo;
        }).orElseThrow(() -> {
            log.warn("Ciclo de avaliação não encontrado: {}", id);
            return new IllegalArgumentException("Ciclo de avaliação não encontrado");
        });
    }

    @Transactional
    public void desativarCiclo(UUID id) {
        log.debug("Desativando ciclo de avaliação: {}", id);

        cicloAvaliacaoRepository.findById(id).ifPresent(ciclo -> {
            if (ciclo.isEmAndamento()) {
                throw new IllegalArgumentException("Não é possível desativar um ciclo em andamento");
            }

            ciclo.setAtivo(false);
            cicloAvaliacaoRepository.save(ciclo);
            log.info("Ciclo de avaliação desativado: {}", id);
        });
    }

    @Transactional
    public CicloAvaliacao iniciarCiclo(UUID id) {
        log.debug("Iniciando ciclo de avaliação: {}", id);

        return cicloAvaliacaoRepository.findById(id).map(ciclo -> {
            if (ciclo.getStatus() != StatusCicloAvaliacao.PLANEJADO) {
                throw new IllegalArgumentException("Só é possível iniciar um ciclo no status 'Planejado'");
            }

            LocalDate hoje = LocalDate.now();
            if (hoje.isBefore(ciclo.getDataInicio())) {
                throw new IllegalArgumentException("A data atual é anterior à data de início do ciclo");
            }

            ciclo.setStatus(StatusCicloAvaliacao.EM_ANDAMENTO);
            CicloAvaliacao cicloAtualizado = cicloAvaliacaoRepository.save(ciclo);

            log.info("Ciclo de avaliação iniciado: {}", id);
            return cicloAtualizado;
        }).orElseThrow(() -> {
            log.warn("Ciclo de avaliação não encontrado: {}", id);
            return new IllegalArgumentException("Ciclo de avaliação não encontrado");
        });
    }

    @Transactional
    public CicloAvaliacao concluirCiclo(UUID id) {
        log.debug("Concluindo ciclo de avaliação: {}", id);

        return cicloAvaliacaoRepository.findById(id).map(ciclo -> {
            // TODO: Verificar se há avaliações pendentes (descomentar quando
            // AvaliacaoDesempenhoService for criado)
            // boolean temAvaliacoesPendentes =
            // avaliacaoDesempenhoService.existemAvaliacoesPendentesNoCiclo(id);
            //
            // if (temAvaliacoesPendentes) {
            // throw new IllegalArgumentException("Não é possível concluir o ciclo pois
            // existem avaliações pendentes");
            // }

            ciclo.setStatus(StatusCicloAvaliacao.CONCLUIDO);
            ciclo.setDataAtualizacao(LocalDateTime.now());
            CicloAvaliacao cicloAtualizado = cicloAvaliacaoRepository.save(ciclo);

            log.info("Ciclo de avaliação concluído: {}", id);
            return cicloAtualizado;
        }).orElseThrow(() -> {
            log.warn("Ciclo de avaliação não encontrado: {}", id);
            return new IllegalArgumentException("Ciclo de avaliação não encontrado");
        });
    }

    @Transactional(readOnly = true)
    public List<CicloAvaliacao> listarCiclosEmAndamento() {
        log.debug("Listando ciclos de avaliação em andamento");
        return cicloAvaliacaoRepository.findCiclosEmAndamento(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<CicloAvaliacao> listarCiclosDoAno(Integer ano) {
        log.debug("Listando ciclos de avaliação do ano: {}", ano);
        return cicloAvaliacaoRepository.findByAno(ano);
    }

    @Transactional(readOnly = true)
    public Optional<CicloAvaliacao> buscarCicloAtivoEmAndamento() {
        log.debug("Buscando ciclo ativo em andamento");
        return cicloAvaliacaoRepository.findCicloAtivoEmAndamento(LocalDate.now());
    }

    // Métodos privados auxiliares
    private void validarDatasCiclo(CicloAvaliacao ciclo) {
        if (ciclo.getDataInicio().isAfter(ciclo.getDataFim())) {
            throw new IllegalArgumentException("Data de início deve ser anterior à data de fim");
        }

        // Validar datas de etapas
        if (ciclo.getDataInicioAutoavaliacao() != null && ciclo.getDataFimAutoavaliacao() != null) {
            if (ciclo.getDataInicioAutoavaliacao().isAfter(ciclo.getDataFimAutoavaliacao())) {
                throw new IllegalArgumentException("Data de início da autoavaliação deve ser anterior à data de fim");
            }

            if (ciclo.getDataInicioAutoavaliacao().isBefore(ciclo.getDataInicio()) ||
                    ciclo.getDataFimAutoavaliacao().isAfter(ciclo.getDataFim())) {
                throw new IllegalArgumentException("Datas da autoavaliação devem estar dentro do período do ciclo");
            }
        }

        if (ciclo.getDataInicioAvaliacaoGestor() != null && ciclo.getDataFimAvaliacaoGestor() != null) {
            if (ciclo.getDataInicioAvaliacaoGestor().isAfter(ciclo.getDataFimAvaliacaoGestor())) {
                throw new IllegalArgumentException(
                        "Data de início da avaliação do gestor deve ser anterior à data de fim");
            }

            if (ciclo.getDataInicioAvaliacaoGestor().isBefore(ciclo.getDataInicio()) ||
                    ciclo.getDataFimAvaliacaoGestor().isAfter(ciclo.getDataFim())) {
                throw new IllegalArgumentException(
                        "Datas da avaliação do gestor devem estar dentro do período do ciclo");
            }
        }

        if (ciclo.getDataInicioFeedback() != null && ciclo.getDataFimFeedback() != null) {
            if (ciclo.getDataInicioFeedback().isAfter(ciclo.getDataFimFeedback())) {
                throw new IllegalArgumentException("Data de início do feedback deve ser anterior à data de fim");
            }

            if (ciclo.getDataInicioFeedback().isBefore(ciclo.getDataInicio()) ||
                    ciclo.getDataFimFeedback().isAfter(ciclo.getDataFim())) {
                throw new IllegalArgumentException("Datas do feedback devem estar dentro do período do ciclo");
            }
        }
    }

    private void configurarDatasAutomaticas(CicloAvaliacao ciclo) {
        LocalDate hoje = LocalDate.now();

        // Se não informadas, configurar datas baseadas no tipo de ciclo
        if (ciclo.getDataInicioAutoavaliacao() == null) {
            ciclo.setDataInicioAutoavaliacao(ciclo.getDataInicio());
        }

        if (ciclo.getDataFimAutoavaliacao() == null) {
            // Autoavaliação termina 7 dias antes do fim da avaliação do gestor
            ciclo.setDataFimAutoavaliacao(ciclo.getDataFim().minusDays(7));
        }

        if (ciclo.getDataInicioAvaliacaoGestor() == null) {
            // Avaliação do gestor começa após a autoavaliação
            ciclo.setDataInicioAvaliacaoGestor(ciclo.getDataFimAutoavaliacao().plusDays(1));
        }

        if (ciclo.getDataFimAvaliacaoGestor() == null) {
            ciclo.setDataFimAvaliacaoGestor(ciclo.getDataFim());
        }

        if (ciclo.getDataInicioFeedback() == null) {
            // Feedback começa após o fim da avaliação do gestor
            ciclo.setDataInicioFeedback(ciclo.getDataFimAvaliacaoGestor().plusDays(1));
        }

        if (ciclo.getDataFimFeedback() == null) {
            // Feedback dura 15 dias
            ciclo.setDataFimFeedback(ciclo.getDataInicioFeedback().plusDays(15));
        }
    }

    private void atualizarStatusCiclo(CicloAvaliacao ciclo) {
        LocalDate hoje = LocalDate.now();

        if (ciclo.getStatus() == StatusCicloAvaliacao.CONCLUIDO ||
                ciclo.getStatus() == StatusCicloAvaliacao.CANCELADO) {
            return; // Não alterar status de ciclos finalizados
        }

        if (hoje.isBefore(ciclo.getDataInicio())) {
            ciclo.setStatus(StatusCicloAvaliacao.PLANEJADO);
        } else if (hoje.isAfter(ciclo.getDataFim())) {
            ciclo.setStatus(StatusCicloAvaliacao.CONCLUIDO);
        } else {
            // Verificar etapa atual
            if (ciclo.isAutoavaliacaoAtiva()) {
                ciclo.setStatus(StatusCicloAvaliacao.EM_AUTOAVALIACAO);
            } else if (ciclo.isAvaliacaoGestorAtiva()) {
                ciclo.setStatus(StatusCicloAvaliacao.EM_AVALIACAO_GESTOR);
            } else if (ciclo.isFeedbackAtivo()) {
                ciclo.setStatus(StatusCicloAvaliacao.EM_FEEDBACK);
            } else {
                ciclo.setStatus(StatusCicloAvaliacao.EM_ANDAMENTO);
            }
        }
    }
}