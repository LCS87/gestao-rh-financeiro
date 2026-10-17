package br.com.callcenter.modulo_comissoes.domain.repository;

import br.com.callcenter.modulo_comissoes.domain.entity.VendaComissao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VendaComissaoRepository extends JpaRepository<VendaComissao, UUID> {

    List<VendaComissao> findByFuncionarioId(UUID funcionarioId);

    List<VendaComissao> findByStatusPagamento(String statusPagamento);

    List<VendaComissao> findByFuncionarioIdAndDataCriacaoBetween(
            UUID funcionarioId, LocalDate inicio, LocalDate fim);

    List<VendaComissao> findByRegraComissaoId(UUID regraComissaoId);
}
