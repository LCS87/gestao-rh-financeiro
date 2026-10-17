package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.ReconhecimentoColaborador;
import br.com.callcenter.modulorh.domain.enums.TipoReconhecimento;
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
public interface ReconhecimentoColaboradorRepository extends JpaRepository<ReconhecimentoColaborador, UUID> {

    Page<ReconhecimentoColaborador> findByAtivoTrueAndPublicoTrueOrderByDataReconhecimentoDesc(Pageable pageable);
    
    List<ReconhecimentoColaborador> findByRemetenteIdAndAtivoTrueOrderByDataReconhecimentoDesc(UUID remetenteId);
    
    List<ReconhecimentoColaborador> findByDestinatarioIdAndAtivoTrueOrderByDataReconhecimentoDesc(UUID destinatarioId);
    
    List<ReconhecimentoColaborador> findByTipoAndAtivoTrueAndPublicoTrueOrderByDataReconhecimentoDesc(TipoReconhecimento tipo);
    
    @Query("SELECT r FROM ReconhecimentoColaborador r WHERE r.ativo = true AND " +
           "(LOWER(r.mensagem) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(r.motivo) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
           "ORDER BY r.dataReconhecimento DESC")
    Page<ReconhecimentoColaborador> searchByTermo(@Param("termo") String termo, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM ReconhecimentoColaborador r WHERE r.ativo = true")
    Long countReconhecimentosAtivos();
    
    @Query("SELECT r.tipo, COUNT(r) FROM ReconhecimentoColaborador r WHERE r.ativo = true GROUP BY r.tipo")
    List<Object[]> countReconhecimentosPorTipo();
    
    @Query("SELECT r.destinatario.id, COUNT(r) FROM ReconhecimentoColaborador r WHERE r.ativo = true GROUP BY r.destinatario.id ORDER BY COUNT(r) DESC")
    List<Object[]> findTopReconhecidos(Pageable pageable);
    
    @Query("SELECT r.remetente.id, COUNT(r) FROM ReconhecimentoColaborador r WHERE r.ativo = true GROUP BY r.remetente.id ORDER BY COUNT(r) DESC")
    List<Object[]> findTopReconhecedores(Pageable pageable);
    
    @Query("SELECT r FROM ReconhecimentoColaborador r WHERE r.ativo = true AND r.dataReconhecimento >= :dataInicio ORDER BY r.dataReconhecimento DESC")
    List<ReconhecimentoColaborador> findReconhecimentosRecentes(@Param("dataInicio") LocalDateTime dataInicio);
}