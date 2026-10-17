package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.VotoEnquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VotoEnqueteRepository extends JpaRepository<VotoEnquete, UUID> {

    Optional<VotoEnquete> findByEnqueteIdAndFuncionarioIdAndOpcaoId(UUID enqueteId, UUID funcionarioId, UUID opcaoId);
    
    List<VotoEnquete> findByEnqueteId(UUID enqueteId);
    
    List<VotoEnquete> findByFuncionarioId(UUID funcionarioId);
    
    List<VotoEnquete> findByOpcaoId(UUID opcaoId);
    
    Long countByEnqueteId(UUID enqueteId);
    
    Long countByOpcaoId(UUID opcaoId);
    
    @Query("SELECT COUNT(v) FROM VotoEnquete v WHERE v.enquete.id = :enqueteId AND v.funcionario.id = :funcionarioId")
    Long countVotosPorFuncionarioEmEnquete(@Param("enqueteId") UUID enqueteId, @Param("funcionarioId") UUID funcionarioId);
    
    @Query("SELECT v.funcionario.id, COUNT(v) FROM VotoEnquete v WHERE v.enquete.id = :enqueteId GROUP BY v.funcionario.id ORDER BY COUNT(v) DESC")
    List<Object[]> findTopVotantesPorEnquete(@Param("enqueteId") UUID enqueteId);
    
    @Query("SELECT v.opcao.id, COUNT(v) FROM VotoEnquete v WHERE v.enquete.id = :enqueteId GROUP BY v.opcao.id")
    List<Object[]> findDistribuicaoVotosPorEnquete(@Param("enqueteId") UUID enqueteId);
}