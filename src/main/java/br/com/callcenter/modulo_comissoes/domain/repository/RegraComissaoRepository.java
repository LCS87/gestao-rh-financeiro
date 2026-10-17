package br.com.callcenter.modulo_comissoes.domain.repository;

import br.com.callcenter.modulo_comissoes.domain.entity.RegraComissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegraComissaoRepository extends JpaRepository<RegraComissao, UUID> {

    List<RegraComissao> findByAtivoTrue();

    List<RegraComissao> findByDepartamentoId(UUID departamentoId);

    @Query("SELECT r FROM RegraComissao r WHERE r.ativo = true AND r.aplicarPorDepartamento = false")
    List<RegraComissao> findGlobais();

    @Query("SELECT r FROM RegraComissao r WHERE r.ativo = true AND r.aplicarPorDepartamento = true AND r.departamento.id = :departamentoId")
    List<RegraComissao> findPorDepartamento(@Param("departamentoId") UUID departamentoId);
}
