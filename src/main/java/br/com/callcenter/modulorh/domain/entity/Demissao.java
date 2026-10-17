package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "demissoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demissao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "funcionario_id", nullable = false, unique = true)
    private UUID funcionarioId;
    
    @Column(name = "data_demissao", nullable = false)
    private LocalDate dataDemissao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_demissao", nullable = false)
    private TipoDemissao tipoDemissao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDemissao status;
    
    @Column(name = "aviso_previo_trabalhado")
    private Boolean avisoPrevioTrabalhado;
    
    @Column(name = "aviso_previo_indenizado")
    private Boolean avisoPrevioIndenizado;
    
    @Column(name = "exame_demissional_realizado")
    private Boolean exameDemissionalRealizado;
    
    @Column(name = "data_exame_demissional")
    private LocalDate dataExameDemissional;
    
    @Column(name = "motivo", length = 1000)
    private String motivo;
    
    @Column(name = "observacoes", length = 2000)
    private String observacoes;
    
    // Cálculo de rescisão
    @Column(name = "saldo_salario", precision = 10, scale = 2)
    private BigDecimal saldoSalario;
    
    @Column(name = "ferias_vencidas", precision = 10, scale = 2)
    private BigDecimal feriasVencidas;
    
    @Column(name = "ferias_proporcionais", precision = 10, scale = 2)
    private BigDecimal feriasProporcionais;
    
    @Column(name = "decimo_terceiro", precision = 10, scale = 2)
    private BigDecimal decimoTerceiro;
    
    @Column(name = "decimo_terceiro_proporcional", precision = 10, scale = 2)
    private BigDecimal decimoTerceiroProporcional;
    
    @Column(name = "aviso_previo_indenizado_valor", precision = 10, scale = 2)
    private BigDecimal avisoPrevioIndenizadoValor;
    
    @Column(name = "fgts", precision = 10, scale = 2)
    private BigDecimal fgts;
    
    @Column(name = "multa_fgts", precision = 10, scale = 2)
    private BigDecimal multaFgts;
    
    @Column(name = "valor_total_rescisao", precision = 10, scale = 2)
    private BigDecimal valorTotalRescisao;
    
    @Column(name = "data_calculo")
    private LocalDate dataCalculo;
    
    @Column(name = "responsavel_demissao_id")
    private UUID responsavelDemissaoId;
    
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (status == null) {
            status = StatusDemissao.INICIADA;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
    
    public enum TipoDemissao {
        SEM_JUSTA_CAUSA,      // Demissão pelo empregador sem justa causa
        COM_JUSTA_CAUSA,      // Demissão pelo empregador com justa causa
        PEDIDO,              // Pedido de demissão pelo funcionário
        TERMINO_CONTRATO     // Término de contrato
    }
    
    public enum StatusDemissao {
        INICIADA, CALCULADA, FINALIZADA, CANCELADA
    }
}
