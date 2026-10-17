package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.entity.Admissao;
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
public class AdmissaoDto {
    private UUID id;
    
    @NotNull(message = "ID do funcionário é obrigatório")
    private UUID funcionarioId;
    
    @NotNull(message = "Data de admissão é obrigatória")
    private LocalDate dataAdmissao;
    
    private Admissao.StatusAdmissao status;
    
    private Boolean rgEntregue;
    private Boolean cpfEntregue;
    private Boolean ctpsEntregue;
    private Boolean comprovanteResidenciaEntregue;
    private Boolean tituloEleitorEntregue;
    private Boolean certificadoReservista;
    private Boolean exameMedicoRealizado;
    private LocalDate exameMedicoValidade;
    private Boolean contratoAssinado;
    private LocalDate dataAssinaturaContrato;
    private String observacoes;
    private UUID responsavelAdmissaoId;
}
