package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.entity.Documento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoDto {
    private UUID id;
    
    @NotNull(message = "ID do funcionário é obrigatório")
    private UUID funcionarioId;
    
    @NotNull(message = "Tipo de documento é obrigatório")
    private Documento.TipoDocumento tipoDocumento;
    
    private String nomeArquivo;
    private String caminhoArquivo;
    private Long tamanhoArquivo;
    private String tipoMime;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;
    private LocalDate dataUpload;
    private Documento.StatusDocumento status;
    private String observacoes;
    private UUID uploadedById;
}
