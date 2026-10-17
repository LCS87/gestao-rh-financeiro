package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.application.service.EngajamentoService;
import br.com.callcenter.modulorh.domain.entity.*;
import br.com.callcenter.modulorh.domain.enums.TipoPostagem;
import br.com.callcenter.modulorh.domain.enums.TipoReacao;
import br.com.callcenter.modulorh.domain.enums.TipoReconhecimento;
import br.com.callcenter.modulorh.domain.enums.VisibilidadePostagem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/engajamento")
@RequiredArgsConstructor
@Tag(name = "Engajamento", description = "API para sistema de comunicação interna e engajamento")
public class EngajamentoController {

    private final EngajamentoService engajamentoService;

    // === POSTAGENS ===
    
    @PostMapping("/postagens")
    @Operation(summary = "Criar nova postagem no mural")
    public ResponseEntity<PostagemMural> criarPostagem(
            @RequestParam UUID autorId,
            @RequestParam String conteudo,
            @RequestParam(required = false) String titulo,
            @RequestParam(defaultValue = "PUBLICACAO") TipoPostagem tipo,
            @RequestParam(defaultValue = "PUBLICO") VisibilidadePostagem visibilidade) {
        
        return ResponseEntity.ok(engajamentoService.criarPostagem(autorId, conteudo, titulo, tipo, visibilidade));
    }

    @GetMapping("/postagens")
    @Operation(summary = "Listar postagens ativas do mural")
    public ResponseEntity<Page<PostagemMural>> listarPostagensAtivas(Pageable pageable) {
        return ResponseEntity.ok(engajamentoService.listarPostagensAtivas(pageable));
    }

    @GetMapping("/postagens/fixadas")
    @Operation(summary = "Listar postagens fixadas")
    public ResponseEntity<Page<PostagemMural>> listarPostagensFixadas(Pageable pageable) {
        return ResponseEntity.ok(engajamentoService.listarPostagensFixadas(pageable));
    }

    @GetMapping("/postagens/autor/{autorId}")
    @Operation(summary = "Listar postagens por autor")
    public ResponseEntity<Page<PostagemMural>> listarPostagensPorAutor(
            @PathVariable UUID autorId, Pageable pageable) {
        
        return ResponseEntity.ok(engajamentoService.listarPostagensPorAutor(autorId, pageable));
    }

    @GetMapping("/postagens/{id}")
    @Operation(summary = "Buscar postagem por ID")
    public ResponseEntity<PostagemMural> buscarPostagemPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(engajamentoService.buscarPostagemPorId(id));
    }

    @PutMapping("/postagens/{id}")
    @Operation(summary = "Atualizar postagem")
    public ResponseEntity<PostagemMural> atualizarPostagem(
            @PathVariable UUID id,
            @RequestParam String conteudo,
            @RequestParam(required = false) String titulo,
            @RequestParam TipoPostagem tipo,
            @RequestParam VisibilidadePostagem visibilidade) {
        
        return ResponseEntity.ok(engajamentoService.atualizarPostagem(id, conteudo, titulo, tipo, visibilidade));
    }

    @PatchMapping("/postagens/{id}/fixar")
    @Operation(summary = "Alternar fixação de postagem")
    public ResponseEntity<Void> alternarFixacaoPostagem(@PathVariable UUID id) {
        engajamentoService.alternarFixacaoPostagem(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/postagens/{id}")
    @Operation(summary = "Desativar postagem")
    public ResponseEntity<Void> desativarPostagem(@PathVariable UUID id) {
        engajamentoService.desativarPostagem(id);
        return ResponseEntity.noContent().build();
    }

    // === COMENTÁRIOS ===
    
    @PostMapping("/postagens/{postagemId}/comentarios")
    @Operation(summary = "Adicionar comentário a postagem")
    public ResponseEntity<ComentarioPostagem> adicionarComentario(
            @PathVariable UUID postagemId,
            @RequestParam UUID autorId,
            @RequestParam String conteudo,
            @RequestParam(required = false) UUID comentarioPaiId) {
        
        return ResponseEntity.ok(engajamentoService.adicionarComentario(postagemId, autorId, conteudo, comentarioPaiId));
    }

    @GetMapping("/postagens/{postagemId}/comentarios")
    @Operation(summary = "Listar comentários de uma postagem")
    public ResponseEntity<List<ComentarioPostagem>> listarComentariosPorPostagem(@PathVariable UUID postagemId) {
        return ResponseEntity.ok(engajamentoService.listarComentariosPorPostagem(postagemId));
    }

    @PutMapping("/comentarios/{id}")
    @Operation(summary = "Atualizar comentário")
    public ResponseEntity<ComentarioPostagem> atualizarComentario(
            @PathVariable UUID id, @RequestParam String conteudo) {
        
        return ResponseEntity.ok(engajamentoService.atualizarComentario(id, conteudo));
    }

    @DeleteMapping("/comentarios/{id}")
    @Operation(summary = "Desativar comentário")
    public ResponseEntity<Void> desativarComentario(@PathVariable UUID id) {
        engajamentoService.desativarComentario(id);
        return ResponseEntity.noContent().build();
    }

    // === CURTIDAS/REAÇÕES ===
    
    @PostMapping("/postagens/{postagemId}/reacoes")
    @Operation(summary = "Adicionar reação a postagem")
    public ResponseEntity<CurtidaPostagem> adicionarReacao(
            @PathVariable UUID postagemId,
            @RequestParam UUID funcionarioId,
            @RequestParam TipoReacao tipoReacao) {
        
        return ResponseEntity.ok(engajamentoService.adicionarReacao(postagemId, funcionarioId, tipoReacao));
    }

    @GetMapping("/postagens/{postagemId}/reacoes")
    @Operation(summary = "Listar reações de uma postagem")
    public ResponseEntity<List<CurtidaPostagem>> listarReacoesPorPostagem(@PathVariable UUID postagemId) {
        return ResponseEntity.ok(engajamentoService.listarReacoesPorPostagem(postagemId));
    }

    @GetMapping("/postagens/{postagemId}/reacoes/distribuicao")
    @Operation(summary = "Obter distribuição de reações por tipo")
    public ResponseEntity<Map<TipoReacao, Long>> obterDistribuicaoReacoes(@PathVariable UUID postagemId) {
        return ResponseEntity.ok(engajamentoService.obterDistribuicaoReacoes(postagemId));
    }

    // === ENQUETES ===
    
    @PostMapping("/enquetes")
    @Operation(summary = "Criar nova enquete")
    public ResponseEntity<EnqueteMural> criarEnquete(
            @RequestParam UUID autorId,
            @RequestParam String pergunta,
            @RequestParam(required = false) String descricao,
            @RequestParam(defaultValue = "false") Boolean anonima,
            @RequestParam(defaultValue = "false") Boolean permiteMultiplaEscolha,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataExpiracao,
            @RequestParam List<String> opcoes) {
        
        return ResponseEntity.ok(engajamentoService.criarEnquete(
                autorId, pergunta, descricao, anonima, permiteMultiplaEscolha, dataExpiracao, opcoes));
    }

    @GetMapping("/enquetes")
    @Operation(summary = "Listar enquetes ativas")
    public ResponseEntity<Page<EnqueteMural>> listarEnquetesAtivas(Pageable pageable) {
        return ResponseEntity.ok(engajamentoService.listarEnquetesAtivas(pageable));
    }

    @GetMapping("/enquetes/{id}")
    @Operation(summary = "Buscar enquete por ID")
    public ResponseEntity<EnqueteMural> buscarEnquetePorId(@PathVariable UUID id) {
        return ResponseEntity.ok(engajamentoService.buscarEnquetePorId(id));
    }

    @PostMapping("/enquetes/{enqueteId}/votar")
    @Operation(summary = "Votar em enquete")
    public ResponseEntity<VotoEnquete> votarEmEnquete(
            @PathVariable UUID enqueteId,
            @RequestParam UUID funcionarioId,
            @RequestParam UUID opcaoId) {
        
        return ResponseEntity.ok(engajamentoService.votarEmEnquete(enqueteId, funcionarioId, opcaoId));
    }

    @GetMapping("/enquetes/{id}/resultados")
    @Operation(summary = "Obter resultados de enquete")
    public ResponseEntity<Map<String, Object>> obterResultadosEnquete(@PathVariable UUID id) {
        return ResponseEntity.ok(engajamentoService.obterResultadosEnquete(id));
    }

    // === RECONHECIMENTO ===
    
    @PostMapping("/reconhecimentos")
    @Operation(summary = "Criar reconhecimento entre colegas")
    public ResponseEntity<ReconhecimentoColaborador> criarReconhecimento(
            @RequestParam UUID remetenteId,
            @RequestParam UUID destinatarioId,
            @RequestParam TipoReconhecimento tipo,
            @RequestParam String mensagem,
            @RequestParam(required = false) String motivo,
            @RequestParam(defaultValue = "true") Boolean publico) {
        
        return ResponseEntity.ok(engajamentoService.criarReconhecimento(
                remetenteId, destinatarioId, tipo, mensagem, motivo, publico));
    }

    @GetMapping("/reconhecimentos")
    @Operation(summary = "Listar reconhecimentos públicos")
    public ResponseEntity<Page<ReconhecimentoColaborador>> listarReconhecimentosPublicos(Pageable pageable) {
        return ResponseEntity.ok(engajamentoService.listarReconhecimentosPublicos(pageable));
    }

    @GetMapping("/reconhecimentos/recebidos/{destinatarioId}")
    @Operation(summary = "Listar reconhecimentos recebidos")
    public ResponseEntity<List<ReconhecimentoColaborador>> listarReconhecimentosRecebidos(
            @PathVariable UUID destinatarioId) {
        
        return ResponseEntity.ok(engajamentoService.listarReconhecimentosRecebidos(destinatarioId));
    }

    @GetMapping("/reconhecimentos/enviados/{remetenteId}")
    @Operation(summary = "Listar reconhecimentos enviados")
    public ResponseEntity<List<ReconhecimentoColaborador>> listarReconhecimentosEnviados(
            @PathVariable UUID remetenteId) {
        
        return ResponseEntity.ok(engajamentoService.listarReconhecimentosEnviados(remetenteId));
    }

    @DeleteMapping("/reconhecimentos/{id}")
    @Operation(summary = "Desativar reconhecimento")
    public ResponseEntity<Void> desativarReconhecimento(@PathVariable UUID id) {
        engajamentoService.desativarReconhecimento(id);
        return ResponseEntity.noContent().build();
    }

    // === DASHBOARD E FEED ===
    
    @GetMapping("/dashboard")
    @Operation(summary = "Obter dashboard de engajamento")
    public ResponseEntity<Map<String, Object>> obterDashboardEngajamento() {
        return ResponseEntity.ok(engajamentoService.obterDashboardEngajamento());
    }

    @GetMapping("/feed/{funcionarioId}")
    @Operation(summary = "Obter feed personalizado para funcionário")
    public ResponseEntity<List<PostagemMural>> obterFeedPersonalizado(
            @PathVariable UUID funcionarioId, Pageable pageable) {
        
        return ResponseEntity.ok(engajamentoService.obterFeedPersonalizado(funcionarioId, pageable));
    }
}