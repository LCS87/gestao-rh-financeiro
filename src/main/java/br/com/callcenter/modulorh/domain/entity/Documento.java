package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "documentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Documento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "funcionario_id", nullable = false)
    private UUID funcionarioId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;
    
    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;
    
    @Column(name = "caminho_arquivo", nullable = false, length = 500)
    private String caminhoArquivo;
    
    @Column(name = "tamanho_arquivo")
    private Long tamanhoArquivo; // em bytes
    
    @Column(name = "tipo_mime")
    private String tipoMime;
    
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;
    
    @Column(name = "data_validade")
    private LocalDate dataValidade;
    
    @Column(name = "data_upload", nullable = false)
    private LocalDate dataUpload;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDocumento status;
    
    @Column(name = "observacoes", length = 1000)
    private String observacoes;
    
    @Column(name = "uploaded_by_id")
    private UUID uploadedById;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (dataUpload == null) {
            dataUpload = LocalDate.now();
        }
        if (status == null) {
            status = StatusDocumento.VALIDO;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
        atualizarStatus();
    }
    
    public void atualizarStatus() {
        if (dataValidade != null) {
            LocalDate hoje = LocalDate.now();
            if (hoje.isAfter(dataValidade)) {
                status = StatusDocumento.VENCIDO;
            } else if (hoje.isAfter(dataValidade.minusDays(30))) {
                status = StatusDocumento.A_VENCER;
            } else {
                status = StatusDocumento.VALIDO;
            }
        } else {
            status = StatusDocumento.VALIDO;
        }
    }
    
    public boolean estaVencido() {
        return dataValidade != null && LocalDate.now().isAfter(dataValidade);
    }
    
    public boolean estaAVencer() {
        return dataValidade != null 
            && LocalDate.now().isBefore(dataValidade) 
            && LocalDate.now().isAfter(dataValidade.minusDays(30));
    }
    
    public enum TipoDocumento {
        RG,
        CPF,
        CTPS,
        COMPROVANTE_RESIDENCIA,
        TITULO_ELEITOR,
        CERTIFICADO_RESERVISTA,
        EXAME_MEDICO,
        EXAME_DEMISSIONAL,
        CONTRATO_TRABALHO,
        TERMO_RESCISAO,
        ATESTADO_MEDICO,
        LICENCA_MEDICA,
        OUTROS
    }
    
    public enum StatusDocumento {
        VALIDO, A_VENCER, VENCIDO, INVALIDO
    }
}
