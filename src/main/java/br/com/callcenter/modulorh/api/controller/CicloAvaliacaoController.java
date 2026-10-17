package br.com.callcenter.modulorh.api.controller;

import br.com.callcenter.modulorh.api.dto.CicloAvaliacaoDTO;
import br.com.callcenter.modulorh.domain.entity.CicloAvaliacao;
import br.com.callcenter.modulorh.domain.service.CicloAvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/ciclos-avaliacao")
@RequiredArgsConstructor
@Tag(name = "Ciclos de Avaliação", description = "API para gerenciamento de ciclos de avaliação de desempenho")
public class CicloAvaliacaoController {

    private final CicloAvaliacaoService cicloAvaliacaoService;

    @GetMapping
    @Operation(summary = "Listar ciclos de avaliação ativos", description = "Retorna todos os ciclos de avaliação ativos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciclos listados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<CicloAvaliacao>> listarCiclosAtivos() {
        log.info("Listando ciclos de avaliação ativos");
        List<CicloAvaliacao> ciclos = cicloAvaliacaoService.listarCiclosAtivos();
        return ResponseEntity.ok(ciclos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ciclo por ID", description = "Retorna um ciclo de avaliação específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciclo encontrado"),
        @ApiResponse(responseCode = "404", description = "Ciclo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CicloAvaliacao> buscarCicloPorId(
            @Parameter(description = "ID do ciclo", required = true)
            @PathVariable UUID id) {
        log.info("Buscando ciclo de avaliação por ID: {}", id);
        return cicloAvaliacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar novo ciclo de avaliação", description = "Cria um novo ciclo de avaliação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ciclo criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "409", description = "Já existe um ciclo com essas características"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CicloAvaliacao> criarCiclo(
            @Parameter(description = "Dados do ciclo a ser criado", required = true)
            @Valid @RequestBody CicloAvaliacaoDTO cicloDTO) {
        log.info("Criando novo ciclo de avaliação: {}", cicloDTO.getNome());
        
        try {
            // TODO: Converter DTO para entidade
            CicloAvaliacao ciclo = new CicloAvaliacao();
            ciclo.setNome(cicloDTO.getNome());
            ciclo.setDescricao(cicloDTO.getDescricao());
            ciclo.setTipo(cicloDTO.getTipo());
            ciclo.setAnoReferencia(cicloDTO.getAnoReferencia());
            ciclo.setPeriodoReferencia(cicloDTO.getPeriodoReferencia());
            ciclo.setDataInicio(cicloDTO.getDataInicio());
            ciclo.setDataFim(cicloDTO.getDataFim());
            ciclo.setDataInicioAutoavaliacao(cicloDTO.getDataInicioAutoavaliacao());
            ciclo.setDataFimAutoavaliacao(cicloDTO.getDataFimAutoavaliacao());
            ciclo.setDataInicioAvaliacaoGestor(cicloDTO.getDataInicioAvaliacaoGestor());
            ciclo.setDataFimAvaliacaoGestor(cicloDTO.getDataFimAvaliacaoGestor());
            ciclo.setDataInicioFeedback(cicloDTO.getDataInicioFeedback());
            ciclo.setDataFimFeedback(cicloDTO.getDataFimFeedback());
            ciclo.setAtivo(cicloDTO.getAtivo());
            
            CicloAvaliacao cicloCriado = cicloAvaliacaoService.criarCiclo(ciclo);
            return ResponseEntity.status(HttpStatus.CREATED).body(cicloCriado);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao criar ciclo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ciclo de avaliação", description = "Atualiza um ciclo de avaliação existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciclo atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "404", description = "Ciclo não encontrado"),
        @ApiResponse(responseCode = "409", description = "Não é possível atualizar ciclo concluído ou cancelado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CicloAvaliacao> atualizarCiclo(
            @Parameter(description = "ID do ciclo a ser atualizado", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Dados atualizados do ciclo", required = true)
            @Valid @RequestBody CicloAvaliacaoDTO cicloDTO) {
        log.info("Atualizando ciclo de avaliação: {}", id);
        
        try {
            // TODO: Converter DTO para entidade
            CicloAvaliacao ciclo = new CicloAvaliacao();
            ciclo.setNome(cicloDTO.getNome());
            ciclo.setDescricao(cicloDTO.getDescricao());
            ciclo.setDataInicio(cicloDTO.getDataInicio());
            ciclo.setDataFim(cicloDTO.getDataFim());
            ciclo.setDataInicioAutoavaliacao(cicloDTO.getDataInicioAutoavaliacao());
            ciclo.setDataFimAutoavaliacao(cicloDTO.getDataFimAutoavaliacao());
            ciclo.setDataInicioAvaliacaoGestor(cicloDTO.getDataInicioAvaliacaoGestor());
            ciclo.setDataFimAvaliacaoGestor(cicloDTO.getDataFimAvaliacaoGestor());
            ciclo.setDataInicioFeedback(cicloDTO.getDataInicioFeedback());
            ciclo.setDataFimFeedback(cicloDTO.getDataFimFeedback());
            ciclo.setAtivo(cicloDTO.getAtivo());
            
            CicloAvaliacao cicloAtualizado = cicloAvaliacaoService.atualizarCiclo(id, ciclo);
            return ResponseEntity.ok(cicloAtualizado);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao atualizar ciclo {}: {}", id, e.getMessage());
            
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("concluído") || e.getMessage().contains("cancelado")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar ciclo de avaliação", description = "Desativa um ciclo de avaliação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ciclo desativado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Não é possível desativar um ciclo em andamento"),
        @ApiResponse(responseCode = "404", description = "Ciclo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> desativarCiclo(
            @Parameter(description = "ID do ciclo a ser desativado", required = true)
            @PathVariable UUID id) {
        log.info("Desativando ciclo de avaliação: {}", id);
        
        try {
            cicloAvaliacaoService.desativarCiclo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro ao desativar ciclo {}: {}", id, e.getMessage());
            
            if (e.getMessage().contains("em andamento")) {
                return ResponseEntity.badRequest().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PostMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar ciclo de avaliação", description = "Inicia um ciclo de avaliação planejado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciclo iniciado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Ciclo não está no status planejado ou data anterior à data de início"),
        @ApiResponse(responseCode = "404", description = "Ciclo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CicloAvaliacao> iniciarCiclo(
            @Parameter(description = "ID do ciclo a ser iniciado", required = true)
            @PathVariable UUID id) {
        log.info("Iniciando ciclo de avaliação: {}", id);
        
        try {
            CicloAvaliacao cicloIniciado = cicloAvaliacaoService.iniciarCiclo(id);
            return ResponseEntity.ok(cicloIniciado);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao iniciar ciclo {}: {}", id, e.getMessage());
            
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @PostMapping("/{id}/concluir")
    @Operation(summary = "Concluir ciclo de avaliação", description = "Conclui um ciclo de avaliação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciclo concluído com sucesso"),
        @ApiResponse(responseCode = "400", description = "Existem avaliações pendentes no ciclo"),
        @ApiResponse(responseCode = "404", description = "Ciclo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CicloAvaliacao> concluirCiclo(
            @Parameter(description = "ID do ciclo a ser concluído", required = true)
            @PathVariable UUID id) {
        log.info("Concluindo ciclo de avaliação: {}", id);
        
        try {
            CicloAvaliacao cicloConcluido = cicloAvaliacaoService.concluirCiclo(id);
            return ResponseEntity.ok(cicloConcluido);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao concluir ciclo {}: {}", id, e.getMessage());
            
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @GetMapping("/em-andamento")
    @Operation(summary = "Listar ciclos em andamento", description = "Retorna todos os ciclos de avaliação que estão em andamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciclos listados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<CicloAvaliacao>> listarCiclosEmAndamento() {
        log.info("Listando ciclos de avaliação em andamento");
        List<CicloAvaliacao> ciclos = cicloAvaliacaoService.listarCiclosEmAndamento();
        return ResponseEntity.ok(ciclos);
    }

    @GetMapping("/ano/{ano}")
    @Operation(summary = "Listar ciclos do ano", description = "Retorna todos os ciclos de avaliação de um ano específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ciclos listados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<CicloAvaliacao>> listarCiclosDoAno(
            @Parameter(description = "Ano para filtro", required = true)
            @PathVariable Integer ano) {
        log.info("Listando ciclos de avaliação do ano: {}", ano);
        List<CicloAvaliacao> ciclos = cicloAvaliacaoService.listarCiclosDoAno(ano);
        return ResponseEntity.ok(ciclos);
    }
}