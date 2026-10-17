package br.com.callcenter.modulofinanceiro.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.callcenter.modulofinanceiro.domain.entity.ContaReceber;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, UUID> {

    List<ContaReceber> findByStatus(ContaReceber.StatusConta status);

    List<ContaReceber> findByCategoria(ContaReceber.CategoriaReceita categoria);

    @Query("SELECT c FROM ContaReceber c WHERE c.dataVencimento BETWEEN :inicio AND :fim ORDER BY c.dataVencimento")
    List<ContaReceber> findByVencimentoBetween(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT c FROM ContaReceber c WHERE c.status = 'PENDENTE' AND c.dataVencimento < :hoje ORDER BY c.dataVencimento")
    List<ContaReceber> findVencidas(@Param("hoje") LocalDate hoje);

    @Query("SELECT c FROM ContaReceber c WHERE c.dataVencimento BETWEEN :inicio AND :fim AND c.status IN ('PENDENTE', 'PARCIAL')")
    List<ContaReceber> findAVencerNoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT SUM(c.valorTotal - COALESCE(c.valorRecebido, 0)) FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL')")
    Double sumValorRestante();

    @Query("SELECT COUNT(c) FROM ContaReceber c WHERE c.status = 'PENDENTE' AND c.dataVencimento = :hoje")
    Long countVencimentosHoje(@Param("hoje") LocalDate hoje);

    List<ContaReceber> findByClienteContainingIgnoreCase(String cliente);

    List<ContaReceber> findByVencimento(LocalDate dataVencimento);

    @Query("SELECT c FROM ContaReceber c WHERE SUBSTRING(c.dataVencimento, 1, 7) = :periodo")
    List<ContaReceber> findByPeriodo(String periodo);
}
