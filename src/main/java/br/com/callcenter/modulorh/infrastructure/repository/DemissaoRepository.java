package br.com.callcenter.modulorh.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.callcenter.modulorh.domain.entity.Demissao;

@Repository
public interface DemissaoRepository extends JpaRepository<Demissao, UUID> {
    Optional<Demissao> findByFuncionarioId(UUID funcionarioId);

    boolean existsByFuncionarioId(UUID funcionarioId);

    List<Demissao> findByDataDemissaoBetween(LocalDate inicio, LocalDate fim);

    long countByDataDemissao(LocalDate dataDemissao);
}
