package br.com.callcenter.modulorh.application.service;

import br.com.callcenter.modulorh.domain.entity.*;
import br.com.callcenter.modulorh.domain.enums.TipoPostagem;
import br.com.callcenter.modulorh.domain.enums.TipoReacao;
import br.com.callcenter.modulorh.domain.enums.TipoReconhecimento;
import br.com.callcenter.modulorh.domain.enums.VisibilidadePostagem;
import br.com.callcenter.modulorh.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EngajamentoService {

    private final PostagemMuralRepository postagemMuralRepository;
    private final ComentarioPostagemRepository comentarioPostagemRepository;
    private final CurtidaPostagemRepository curtidaPostagemRepository;
    private final EnqueteMuralRepository enqueteMuralRepository;
    private final OpcaoEnqueteRepository opcaoEnqueteRepository;
    private final VotoEnqueteRepository votoEnqueteRepository;
    private final ReconhecimentoColaboradorRepository reconhecimentoColaboradorRepository;
    private final FuncionarioRepository funcionarioRepository;

    // === POSTAGENS NO MURAL ===
    
    @Transactional
    public PostagemMural criarPostagem(UUID autorId, String conteudo, String titulo, 
                                      TipoPostagem tipo, VisibilidadePostagem visibilidade) {
        
        Funcionario autor = funcionarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + autorId));

        PostagemMural postagem = new PostagemMural(autor, conteudo, tipo);
        postagem.setTitulo(titulo);
        postagem.setVisibilidade(visibilidade);

        return postagemMuralRepository.save(postagem);
    }

    public Page<PostagemMural> listarPostagensAtivas(Pageable pageable) {
        return postagemMuralRepository.findByAtivaTrueOrderByDataPublicacaoDesc(pageable);
    }

    public Page<PostagemMural> listarPostagensFixadas(Pageable pageable) {
        return postagemMuralRepository.findByAtivaTrueAndFixadaTrueOrderByDataPublicacaoDesc(pageable);
    }

    public Page<PostagemMural> listarPostagensPorAutor(UUID autorId, Pageable pageable) {
        return postagemMuralRepository.findByAutorIdAndAtivaTrueOrderByDataPublicacaoDesc(autorId, pageable);
    }

    public PostagemMural buscarPostagemPorId(UUID id) {
        return postagemMuralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postagem não encontrada com ID: " + id));
    }

    @Transactional
    public PostagemMural atualizarPostagem(UUID id, String conteudo, String titulo, 
                                          TipoPostagem tipo, VisibilidadePostagem visibilidade) {
        
        PostagemMural postagem = buscarPostagemPorId(id);
        
        postagem.setConteudo(conteudo);
        postagem.setTitulo(titulo);
        postagem.setTipo(tipo);
        postagem.setVisibilidade(visibilidade);

        return postagemMuralRepository.save(postagem);
    }

    @Transactional
    public void alternarFixacaoPostagem(UUID id) {
        PostagemMural postagem = buscarPostagemPorId(id);
        postagem.setFixada(!postagem.getFixada());
        postagemMuralRepository.save(postagem);
        
        log.info("Postagem {} {}", id, postagem.getFixada() ? "fixada" : "desfixada");
    }

    @Transactional
    public void desativarPostagem(UUID id) {
        PostagemMural postagem = buscarPostagemPorId(id);
        postagem.setAtiva(false);
        postagemMuralRepository.save(postagem);
        log.info("Postagem desativada: {}", id);
    }

    // === COMENTÁRIOS ===
    
    @Transactional
    public ComentarioPostagem adicionarComentario(UUID postagemId, UUID autorId, String conteudo, UUID comentarioPaiId) {
        
        PostagemMural postagem = buscarPostagemPorId(postagemId);
        Funcionario autor = funcionarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + autorId));

        ComentarioPostagem comentarioPai = null;
        if (comentarioPaiId != null) {
            comentarioPai = comentarioPostagemRepository.findById(comentarioPaiId)
                    .orElseThrow(() -> new RuntimeException("Comentário pai não encontrado com ID: " + comentarioPaiId));
        }

        ComentarioPostagem comentario = new ComentarioPostagem(postagem, autor, conteudo, comentarioPai);
        return comentarioPostagemRepository.save(comentario);
    }

    public List<ComentarioPostagem> listarComentariosPorPostagem(UUID postagemId) {
        return comentarioPostagemRepository.findComentariosHierarquicosPorPostagem(postagemId);
    }

    @Transactional
    public ComentarioPostagem atualizarComentario(UUID id, String conteudo) {
        ComentarioPostagem comentario = comentarioPostagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado com ID: " + id));

        comentario.setConteudo(conteudo);
        return comentarioPostagemRepository.save(comentario);
    }

    @Transactional
    public void desativarComentario(UUID id) {
        ComentarioPostagem comentario = comentarioPostagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado com ID: " + id));

        comentario.setAtivo(false);
        comentarioPostagemRepository.save(comentario);
        log.info("Comentário desativado: {}", id);
    }

    // === CURTIDAS/REAÇÕES ===
    
    @Transactional
    public CurtidaPostagem adicionarReacao(UUID postagemId, UUID funcionarioId, TipoReacao tipoReacao) {
        
        PostagemMural postagem = buscarPostagemPorId(postagemId);
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + funcionarioId));

        // Verificar se já existe reação do mesmo funcionário
        Optional<CurtidaPostagem> reacaoExistente = curtidaPostagemRepository
                .findByPostagemIdAndFuncionarioId(postagemId, funcionarioId);

        if (reacaoExistente.isPresent()) {
            CurtidaPostagem reacao = reacaoExistente.get();
            if (reacao.getTipoReacao() == tipoReacao) {
                // Se já existe a mesma reação, remove (toggle)
                curtidaPostagemRepository.delete(reacao);
                log.info("Reação removida: {} - {}", postagemId, funcionarioId);
                return null;
            } else {
                // Se existe reação diferente, atualiza
                reacao.setTipoReacao(tipoReacao);
                reacao.setDataCurtida(LocalDateTime.now());
                return curtidaPostagemRepository.save(reacao);
            }
        }

        CurtidaPostagem novaReacao = new CurtidaPostagem(postagem, funcionario, tipoReacao);
        return curtidaPostagemRepository.save(novaReacao);
    }

    public List<CurtidaPostagem> listarReacoesPorPostagem(UUID postagemId) {
        return curtidaPostagemRepository.findByPostagemId(postagemId);
    }

    public Map<TipoReacao, Long> obterDistribuicaoReacoes(UUID postagemId) {
        List<Object[]> resultados = curtidaPostagemRepository.findDistribuicaoReacoesPorPostagem(postagemId);
        
        Map<TipoReacao, Long> distribuicao = new EnumMap<>(TipoReacao.class);
        for (TipoReacao tipo : TipoReacao.values()) {
            distribuicao.put(tipo, 0L);
        }
        
        for (Object[] resultado : resultados) {
            TipoReacao tipo = (TipoReacao) resultado[0];
            Long quantidade = (Long) resultado[1];
            distribuicao.put(tipo, quantidade);
        }
        
        return distribuicao;
    }

    // === ENQUETES ===
    
    @Transactional
    public EnqueteMural criarEnquete(UUID autorId, String pergunta, String descricao, 
                                    Boolean anonima, Boolean permiteMultiplaEscolha, 
                                    LocalDateTime dataExpiracao, List<String> opcoes) {
        
        Funcionario autor = funcionarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + autorId));

        EnqueteMural enquete = new EnqueteMural(autor, pergunta, anonima);
        enquete.setDescricao(descricao);
        enquete.setPermiteMultiplaEscolha(permiteMultiplaEscolha);
        enquete.setDataExpiracao(dataExpiracao);

        EnqueteMural enqueteSalva = enqueteMuralRepository.save(enquete);

        // Adicionar opções
        for (int i = 0; i < opcoes.size(); i++) {
            OpcaoEnquete opcao = new OpcaoEnquete(enqueteSalva, opcoes.get(i), i);
            opcaoEnqueteRepository.save(opcao);
        }

        return enqueteSalva;
    }

    public Page<EnqueteMural> listarEnquetesAtivas(Pageable pageable) {
        return enqueteMuralRepository.findByAtivaTrueOrderByDataCriacaoDesc(pageable);
    }

    public EnqueteMural buscarEnquetePorId(UUID id) {
        return enqueteMuralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquete não encontrada com ID: " + id));
    }

    @Transactional
    public VotoEnquete votarEmEnquete(UUID enqueteId, UUID funcionarioId, UUID opcaoId) {
        
        EnqueteMural enquete = buscarEnquetePorId(enqueteId);
        
        if (enquete.getDataExpiracao() != null && enquete.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Enquete expirada. Não é possível votar.");
        }

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + funcionarioId));

        OpcaoEnquete opcao = opcaoEnqueteRepository.findById(opcaoId)
                .orElseThrow(() -> new RuntimeException("Opção não encontrada com ID: " + opcaoId));

        if (!opcao.getEnquete().getId().equals(enqueteId)) {
            throw new RuntimeException("Opção não pertence a esta enquete");
        }

        // Verificar se já votou nesta opção
        Optional<VotoEnquete> votoExistente = votoEnqueteRepository
                .findByEnqueteIdAndFuncionarioIdAndOpcaoId(enqueteId, funcionarioId, opcaoId);

        if (votoExistente.isPresent()) {
            throw new RuntimeException("Você já votou nesta opção");
        }

        // Verificar se permite múltipla escolha
        if (!enquete.getPermiteMultiplaEscolha()) {
            Long votosExistentes = votoEnqueteRepository.countVotosPorFuncionarioEmEnquete(enqueteId, funcionarioId);
            if (votosExistentes > 0) {
                throw new RuntimeException("Esta enquete não permite múltipla escolha");
            }
        }

        VotoEnquete voto = new VotoEnquete(enquete, funcionario, opcao);
        return votoEnqueteRepository.save(voto);
    }

    public Map<String, Object> obterResultadosEnquete(UUID enqueteId) {
        EnqueteMural enquete = buscarEnquetePorId(enqueteId);
        List<Object[]> distribuicao = votoEnqueteRepository.findDistribuicaoVotosPorEnquete(enqueteId);
        Long totalVotos = votoEnqueteRepository.countByEnqueteId(enqueteId);

        Map<String, Object> resultados = new HashMap<>();
        resultados.put("enquete", enquete);
        resultados.put("totalVotos", totalVotos);
        
        List<Map<String, Object>> opcoesComVotos = new ArrayList<>();
        for (OpcaoEnquete opcao : opcaoEnqueteRepository.findByEnqueteIdOrderByOrdemAsc(enqueteId)) {
            Map<String, Object> opcaoMap = new HashMap<>();
            opcaoMap.put("opcao", opcao);
            opcaoMap.put("totalVotos", distribuicao.stream()
                    .filter(d -> d[0].equals(opcao.getId()))
                    .map(d -> (Long) d[1])
                    .findFirst()
                    .orElse(0L));
            opcaoMap.put("percentual", totalVotos > 0 ? 
                    ((double) opcaoMap.get("totalVotos") / totalVotos * 100) : 0.0);
            opcoesComVotos.add(opcaoMap);
        }
        
        resultados.put("opcoes", opcoesComVotos);
        return resultados;
    }

    // === RECONHECIMENTO ENTRE COLEGAS ===
    
    @Transactional
    public ReconhecimentoColaborador criarReconhecimento(UUID remetenteId, UUID destinatarioId, 
                                                        TipoReconhecimento tipo, String mensagem, 
                                                        String motivo, Boolean publico) {
        
        if (remetenteId.equals(destinatarioId)) {
            throw new RuntimeException("Não é possível se reconhecer");
        }

        Funcionario remetente = funcionarioRepository.findById(remetenteId)
                .orElseThrow(() -> new RuntimeException("Remetente não encontrado com ID: " + remetenteId));

        Funcionario destinatario = funcionarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new RuntimeException("Destinatário não encontrado com ID: " + destinatarioId));

        ReconhecimentoColaborador reconhecimento = new ReconhecimentoColaborador(remetente, destinatario, tipo, mensagem);
        reconhecimento.setMotivo(motivo);
        reconhecimento.setPublico(publico);

        return reconhecimentoColaboradorRepository.save(reconhecimento);
    }

    public Page<ReconhecimentoColaborador> listarReconhecimentosPublicos(Pageable pageable) {
        return reconhecimentoColaboradorRepository.findByAtivoTrueAndPublicoTrueOrderByDataReconhecimentoDesc(pageable);
    }

    public List<ReconhecimentoColaborador> listarReconhecimentosRecebidos(UUID destinatarioId) {
        return reconhecimentoColaboradorRepository.findByDestinatarioIdAndAtivoTrueOrderByDataReconhecimentoDesc(destinatarioId);
    }

    public List<ReconhecimentoColaborador> listarReconhecimentosEnviados(UUID remetenteId) {
        return reconhecimentoColaboradorRepository.findByRemetenteIdAndAtivoTrueOrderByDataReconhecimentoDesc(remetenteId);
    }

    @Transactional
    public void desativarReconhecimento(UUID id) {
        ReconhecimentoColaborador reconhecimento = reconhecimentoColaboradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reconhecimento não encontrado com ID: " + id));

        reconhecimento.setAtivo(false);
        reconhecimentoColaboradorRepository.save(reconhecimento);
        log.info("Reconhecimento desativado: {}", id);
    }

    // === DASHBOARD E MÉTRICAS ===
    
    public Map<String, Object> obterDashboardEngajamento() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Totais
        dashboard.put("totalPostagens", postagemMuralRepository.countPostagensAtivas());
        dashboard.put("totalComentarios", comentarioPostagemRepository.count());
        dashboard.put("totalCurtidas", curtidaPostagemRepository.count());
        dashboard.put("totalEnquetes", enqueteMuralRepository.countEnquetesAtivas());
        dashboard.put("totalReconhecimentos", reconhecimentoColaboradorRepository.countReconhecimentosAtivos());
        
        // Postagens mais comentadas
        List<Object[]> postagensComentadas = comentarioPostagemRepository.findPostagensMaisComentadas(Pageable.ofSize(5));
        dashboard.put("postagensMaisComentadas", postagensComentadas);
        
        // Postagens mais curtidas
        List<Object[]> postagensCurtidas = curtidaPostagemRepository.findPostagensMaisCurtidas(Pageable.ofSize(5));
        dashboard.put("postagensMaisCurtidas", postagensCurtidas);
        
        // Enquetes mais votadas
        List<Object[]> enquetesVotadas = enqueteMuralRepository.findEnquetesMaisVotadas(Pageable.ofSize(5));
        dashboard.put("enquetesMaisVotadas", enquetesVotadas);
        
        // Top reconhecidos
        List<Object[]> topReconhecidos = reconhecimentoColaboradorRepository.findTopReconhecidos(Pageable.ofSize(10));
        dashboard.put("topReconhecidos", topReconhecidos);
        
        // Top reconhecedores
        List<Object[]> topReconhecedores = reconhecimentoColaboradorRepository.findTopReconhecedores(Pageable.ofSize(10));
        dashboard.put("topReconhecedores", topReconhecedores);
        
        // Distribuição por tipo de postagem
        List<Object[]> distribPostagens = postagemMuralRepository.countPostagensPorTipo();
        dashboard.put("distribuicaoPostagens", distribPostagens);
        
        // Distribuição por tipo de reconhecimento
        List<Object[]> distribReconhecimentos = reconhecimentoColaboradorRepository.countReconhecimentosPorTipo();
        dashboard.put("distribuicaoReconhecimentos", distribReconhecimentos);
        
        return dashboard;
    }

    public List<PostagemMural> obterFeedPersonalizado(UUID funcionarioId, Pageable pageable) {
        // Lógica para feed personalizado baseado em departamento, interesses, etc.
        // Por enquanto, retorna postagens públicas e do departamento do funcionário
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
        
        return postagemMuralRepository.findByAtivaTrueOrderByDataPublicacaoDesc(pageable)
                .getContent()
                .stream()
                .filter(p -> p.getVisibilidade() == VisibilidadePostagem.PUBLICO)
                .collect(Collectors.toList());
    }
}