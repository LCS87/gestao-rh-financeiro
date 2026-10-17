package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.DocumentoDto;
import br.com.callcenter.modulorh.application.DocumentoService;
import br.com.callcenter.modulorh.domain.entity.Documento;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<List<Documento>> listar() {
        return ResponseEntity.ok(documentoService.listarTodos());
    }

    @GetMapping("/funcionario/{funcionarioId}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Documento>> listarPorFuncionario(@PathVariable UUID funcionarioId) {
        return ResponseEntity.ok(documentoService.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Documento> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(documentoService.buscarPorId(id));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Documento> upload(
            @RequestParam("funcionarioId") UUID funcionarioId,
            @RequestParam("tipoDocumento") Documento.TipoDocumento tipoDocumento,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "dataEmissao", required = false) LocalDate dataEmissao,
            @RequestParam(value = "dataValidade", required = false) LocalDate dataValidade,
            @RequestParam(value = "observacoes", required = false) String observacoes,
            @RequestParam(value = "uploadedById", required = false) UUID uploadedById) {
        
        Documento documento = documentoService.uploadDocumento(
                funcionarioId, tipoDocumento, arquivo, dataEmissao, dataValidade, observacoes, uploadedById);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(documento);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        Documento documento = documentoService.buscarPorId(id);
        byte[] arquivo = documentoService.downloadDocumento(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(documento.getTipoMime()));
        headers.setContentDispositionFormData("attachment", documento.getNomeArquivo());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(arquivo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Documento> atualizar(@PathVariable UUID id,
                                               @Valid @RequestBody DocumentoDto dto) {
        return ResponseEntity.ok(documentoService.atualizar(id, dto));
    }

    @GetMapping("/a-vencer")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<List<Documento>> buscarAVencer(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(documentoService.buscarDocumentosAVencer(dias));
    }

    @GetMapping("/vencidos")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<List<Documento>> buscarVencidos() {
        return ResponseEntity.ok(documentoService.buscarDocumentosVencidos());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        documentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
