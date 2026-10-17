package br.com.callcenter.modulofinanceiro.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.callcenter.modulofinanceiro.domain.entity.ContaPagar;

@Repository
public interface ContaPagarRepository extends JpaRepository<ContaPagar, UUID> {

    List<ContaPagar> findByStatus(ContaPagar.StatusConta status);

    List<ContaPagar> findByCategoria(ContaPagar.CategoriaConta categoria);

    @Query("SELECT c FROM ContaPagar c WHERE c.dataVencimento BETWEEN :inicio AND :fim ORDER BY c.dataVencimento")
    List<ContaPagar> findByVencimentoBetween(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT c FROM ContaPagar c WHERE c.status = 'PENDENTE' AND c.dataVencimento < :hoje ORDER BY c.dataVencimento")
    List<ContaPagar> findVencidas(@Param("hoje") LocalDate hoje);

    @Query("SELECT c FROM ContaPagar c WHERE c.dataVencimento BETWEEN :inicio AND :fim AND c.status IN ('PENDENTE', 'PARCIAL')")
    List<ContaPagar> findAVencerNoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT SUM(c.valorTotal - COALESCE(c.valorPago, 0)) FROM ContaPagar c WHERE c.status IN ('PENDENTE', 'PARCIAL')")
    Double sumValorRestante();

    @Query("SELECT COUNT(c) FROM ContaPagar c WHERE c.status = 'PENDENTE' AND c.dataVencimento = :hoje")
    Long countVencimentosHoje(@Param("hoje") LocalDate hoje);

    List<ContaPagar> findByFornecedorContainingIgnoreCase(String fornecedor);

    List<ContaPagar> findByVencimento(LocalDate dataVencimento);

    @Query("SELECT c FROM ContaPagar c WHERE SUBSTRING(c.dataVencimento, 1, 7) = :periodo")
    List<ContaPagar> findByPeriodo(String periodo);
}
