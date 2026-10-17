package br.com.callcenter.modulorh.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.callcenter.modulorh.domain.entity.Ferias;

public interface FeriasRepository extends JpaRepository<Ferias, UUID> {

        List<Ferias> findByFuncionarioIdOrderByDataInicioDesc(UUID funcionarioId);

        @Query("""
                        SELECT COUNT(f) FROM Ferias f
                        WHERE f.dataInicio <= :fim AND f.dataFim >= :inicio
                        """)
        long countByDataInicioBeforeAndDataFimAfterOrDataInicioBetween(
                        @Param("inicio") LocalDate inicio,
                        @Param("fim") LocalDate fim);

        long countByDataInicio(LocalDate data);

        long countByDataInicioBetween(LocalDate inicio, LocalDate fim);
}
