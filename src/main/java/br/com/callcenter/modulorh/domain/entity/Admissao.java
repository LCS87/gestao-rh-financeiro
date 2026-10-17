package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "admissoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admissao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "funcionario_id", nullable = false, unique = true)
    private UUID funcionarioId;
    
    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAdmissao status;
    
    @Column(name = "rg_entregue")
    private Boolean rgEntregue;
    
    @Column(name = "cpf_entregue")
    private Boolean cpfEntregue;
    
    @Column(name = "ctps_entregue")
    private Boolean ctpsEntregue;
    
    @Column(name = "comprovante_residencia_entregue")
    private Boolean comprovanteResidenciaEntregue;
    
    @Column(name = "titulo_eleitor_entregue")
    private Boolean tituloEleitorEntregue;
    
    @Column(name = "certificado_reservista")
    private Boolean certificadoReservista;
    
    @Column(name = "exame_medico_realizado")
    private Boolean exameMedicoRealizado;
    
    @Column(name = "exame_medico_validade")
    private LocalDate exameMedicoValidade;
    
    @Column(name = "contrato_assinado")
    private Boolean contratoAssinado;
    
    @Column(name = "data_assinatura_contrato")
    private LocalDate dataAssinaturaContrato;
    
    @Column(name = "observacoes", length = 1000)
    private String observacoes;
    
    @Column(name = "responsavel_admissao_id")
    private UUID responsavelAdmissaoId;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (status == null) {
            status = StatusAdmissao.PENDENTE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
    
    public boolean isCompleta() {
        return rgEntregue != null && rgEntregue
            && cpfEntregue != null && cpfEntregue
            && ctpsEntregue != null && ctpsEntregue
            && comprovanteResidenciaEntregue != null && comprovanteResidenciaEntregue
            && exameMedicoRealizado != null && exameMedicoRealizado
            && contratoAssinado != null && contratoAssinado;
    }
    
    public enum StatusAdmissao {
        PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA
    }
}
