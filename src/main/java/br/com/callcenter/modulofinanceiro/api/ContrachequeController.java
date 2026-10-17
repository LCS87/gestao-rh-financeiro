package br.com.callcenter.modulofinanceiro.api;

import br.com.callcenter.modulofinanceiro.application.ContrachequeService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/financeiro/contracheques")
public class ContrachequeController {

    private final ContrachequeService contrachequeService;

    public ContrachequeController(ContrachequeService contrachequeService) {
        this.contrachequeService = contrachequeService;
    }

    @GetMapping("/{folhaId}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<ByteArrayResource> gerarContrachequePdf(@PathVariable UUID folhaId) {
        try {
            byte[] pdfBytes = contrachequeService.gerarContracheque(folhaId);
            
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            String filename = "contracheque_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{folhaId}/pdf/inline")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<ByteArrayResource> visualizarContrachequePdf(@PathVariable UUID folhaId) {
        try {
            byte[] pdfBytes = contrachequeService.gerarContracheque(folhaId);
            
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"contracheque.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/mes/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<List<Map<String, Object>>> listarContrachequesPorMes(@PathVariable String anoMes) {
        return ResponseEntity.ok(contrachequeService.listarContrachequesPorMes(anoMes));
    }

    @GetMapping("/funcionario/{funcionarioId}/mes/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<List<Map<String, Object>>> listarContrachequesFuncionarioPorMes(
            @PathVariable UUID funcionarioId,
            @PathVariable String anoMes) {
        List<Map<String, Object>> todos = contrachequeService.listarContrachequesPorMes(anoMes);
        
        List<Map<String, Object>> filtrados = todos.stream()
                .filter(item -> funcionarioId.equals(item.get("funcionarioId")))
                .toList();
        
        return ResponseEntity.ok(filtrados);
    }
}
