package br.com.callcenter.modulorelatorios.domain.repository;

import br.com.callcenter.modulorelatorios.domain.entity.FluxoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FluxoCaixaRepository extends JpaRepository<FluxoCaixa, UUID> {

    List<FluxoCaixa> findByPeriodo(String periodo);

    List<FluxoCaixa> findByTipo(String tipo);

    List<FluxoCaixa> findByCategoria(String categoria);

    List<FluxoCaixa> findByDataBetween(LocalDate inicio, LocalDate fim);

    @Query("""
            SELECT fc FROM FluxoCaixa fc
            WHERE fc.periodo = :periodo
            AND fc.tipo = :tipo
            """)
    List<FluxoCaixa> findByPeriodoAndTipo(
            @Param("periodo") String periodo,
            @Param("tipo") String tipo);
}
