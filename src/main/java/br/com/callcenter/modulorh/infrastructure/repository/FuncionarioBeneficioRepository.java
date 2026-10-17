package br.com.callcenter.modulorh.infrastructure.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.callcenter.modulorh.domain.entity.FuncionarioBeneficio;

@Repository
public interface FuncionarioBeneficioRepository extends JpaRepository<FuncionarioBeneficio, UUID> {

       List<FuncionarioBeneficio> findByFuncionarioIdAndAtivoTrue(UUID funcionarioId);

       List<FuncionarioBeneficio> findByBeneficioIdAndAtivoTrue(UUID beneficioId);

       Optional<FuncionarioBeneficio> findByFuncionarioIdAndBeneficioIdAndAtivoTrue(
                     UUID funcionarioId, UUID beneficioId);

       @Query("SELECT fb FROM FuncionarioBeneficio fb " +
                     "WHERE fb.funcionario.id = :funcionarioId " +
                     "AND fb.ativo = true " +
                     "AND (fb.dataFim IS NULL OR fb.dataFim >= :dataReferencia) " +
                     "AND fb.dataInicio <= :dataReferencia")
       List<FuncionarioBeneficio> findBeneficiosAtivosPorFuncionarioNaData(
                     @Param("funcionarioId") UUID funcionarioId,
                     @Param("dataReferencia") LocalDate dataReferencia);

       @Query("SELECT fb FROM FuncionarioBeneficio fb " +
                     "WHERE fb.beneficio.id = :beneficioId " +
                     "AND fb.ativo = true " +
                     "AND (fb.dataFim IS NULL OR fb.dataFim >= CURRENT_DATE) " +
                     "AND fb.dataInicio <= CURRENT_DATE")
       List<FuncionarioBeneficio> findFuncionariosAtivosPorBeneficio(@Param("beneficioId") UUID beneficioId);

       @Query("SELECT COUNT(fb) FROM FuncionarioBeneficio fb " +
                     "WHERE fb.ativo = true " +
                     "AND (fb.dataFim IS NULL OR fb.dataFim >= CURRENT_DATE) " +
                     "AND fb.dataInicio <= CURRENT_DATE")
       Long countBeneficiosAtivos();

       @Query("SELECT SUM(fb.valorEmpresa) FROM FuncionarioBeneficio fb " +
                     "WHERE fb.ativo = true " +
                     "AND (fb.dataFim IS NULL OR fb.dataFim >= :mesReferencia) " +
                     "AND fb.dataInicio <= :mesReferencia " +
                     "AND EXTRACT(MONTH FROM fb.dataInicio) = EXTRACT(MONTH FROM :mesReferencia) " +
                     "AND EXTRACT(YEAR FROM fb.dataInicio) = EXTRACT(YEAR FROM :mesReferencia)")
       BigDecimal sumCustoEmpresaPorMes(@Param("mesReferencia") LocalDate mesReferencia);

       @Query("SELECT fb.beneficio.tipo, COUNT(fb), SUM(fb.valorEmpresa), SUM(fb.valorFuncionario) " +
                     "FROM FuncionarioBeneficio fb " +
                     "WHERE fb.ativo = true " +
                     "AND (fb.dataFim IS NULL OR fb.dataFim >= CURRENT_DATE) " +
                     "AND fb.dataInicio <= CURRENT_DATE " +
                     "GROUP BY fb.beneficio.tipo")
       List<Object[]> findResumoBeneficiosAtivos();
}