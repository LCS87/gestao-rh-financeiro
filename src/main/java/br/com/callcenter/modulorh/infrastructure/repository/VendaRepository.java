package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VendaRepository extends JpaRepository<Venda, UUID> {

    List<Venda> findByVendedorId(UUID vendedorId);

    // Soma das vendas de um vendedor em um período
    @Query("SELECT COALESCE(SUM(v.valor), 0) FROM Venda v WHERE v.vendedor.id = :vendedorId AND v.dataVenda >= :inicio AND v.dataVenda <= :fim")
    Double sumValorByVendedorIdAndPeriodo(@Param("vendedorId") UUID vendedorId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Soma das vendas globais em um período
    @Query("SELECT COALESCE(SUM(v.valor), 0) FROM Venda v WHERE v.dataVenda >= :inicio AND v.dataVenda <= :fim")
    Double sumValorByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT v.vendedor.id as vendedorId, v.vendedor.nome as vendedorNome, COALESCE(SUM(v.valor), 0) as total FROM Venda v WHERE v.dataVenda >= :inicio AND v.dataVenda <= :fim GROUP BY v.vendedor.id, v.vendedor.nome")
    List<Object[]> queryRankingVendas(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // native query to get history for a specific seller
    @Query(value = "SELECT CAST(data_venda AS DATE) as dataStr, SUM(valor) as total FROM vendas WHERE funcionario_id = :vendedorId AND data_venda >= :inicio GROUP BY CAST(data_venda AS DATE) ORDER BY dataStr ASC", nativeQuery = true)
    List<Object[]> findHistoricoByVendedor(@Param("vendedorId") UUID vendedorId, @Param("inicio") LocalDateTime inicio);

    // native query to get global history
    @Query(value = "SELECT CAST(data_venda AS DATE) as dataStr, SUM(valor) as total FROM vendas WHERE data_venda >= :inicio GROUP BY CAST(data_venda AS DATE) ORDER BY dataStr ASC", nativeQuery = true)
    List<Object[]> findHistoricoGlobal(@Param("inicio") LocalDateTime inicio);
}
