package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.EnqueteMural;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnqueteMuralRepository extends JpaRepository<EnqueteMural, UUID> {

    Page<EnqueteMural> findByAtivaTrueOrderByDataCriacaoDesc(Pageable pageable);
    
    List<EnqueteMural> findByAtivaTrueAndDataExpiracaoIsNullOrDataExpiracaoAfter(LocalDateTime dataAtual);
    
    List<EnqueteMural> findByAutorIdAndAtivaTrueOrderByDataCriacaoDesc(UUID autorId);
    
    List<EnqueteMural> findByAnonimaTrueAndAtivaTrueOrderByDataCriacaoDesc();
    
    @Query("SELECT e FROM EnqueteMural e WHERE e.ativa = true AND " +
           "(LOWER(e.pergunta) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(e.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
           "ORDER BY e.dataCriacao DESC")
    Page<EnqueteMural> searchByTermo(@Param("termo") String termo, Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM EnqueteMural e WHERE e.ativa = true")
    Long countEnquetesAtivas();
    
    @Query("SELECT e.autor.id, COUNT(e) FROM EnqueteMural e WHERE e.ativa = true GROUP BY e.autor.id ORDER BY COUNT(e) DESC")
    List<Object[]> findTopAutoresEnquetes(Pageable pageable);
    
    @Query("SELECT e.id, COUNT(v) as totalVotos FROM EnqueteMural e " +
           "LEFT JOIN e.votos v WHERE e.ativa = true GROUP BY e.id ORDER BY totalVotos DESC")
    List<Object[]> findEnquetesMaisVotadas(Pageable pageable);
    
    @Query("SELECT e FROM EnqueteMural e WHERE e.ativa = true AND e.dataExpiracao IS NOT NULL AND e.dataExpiracao < :dataAtual")
    List<EnqueteMural> findEnquetesExpiradas(@Param("dataAtual") LocalDateTime dataAtual);
}