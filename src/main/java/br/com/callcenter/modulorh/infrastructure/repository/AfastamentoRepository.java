package br.com.callcenter.modulorh.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.callcenter.modulorh.domain.entity.Afastamento;

public interface AfastamentoRepository extends JpaRepository<Afastamento, UUID> {

        List<Afastamento> findByFuncionarioIdOrderByDataInicioDesc(UUID funcionarioId);

        @Query("""
                        SELECT COUNT(a) FROM Afastamento a
                        WHERE a.dataInicio <= :fim AND a.dataFim >= :inicio
                        """)
        long countByDataInicioBeforeAndDataFimAfterOrDataInicioBetween(
                        @Param("inicio") LocalDate inicio,
                        @Param("fim") LocalDate fim);

        long countByDataInicioBetween(LocalDate inicio, LocalDate fim);

        long countByDataInicio(LocalDate data);
}
