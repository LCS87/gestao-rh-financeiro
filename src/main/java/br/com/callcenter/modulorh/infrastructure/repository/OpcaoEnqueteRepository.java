package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.OpcaoEnquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OpcaoEnqueteRepository extends JpaRepository<OpcaoEnquete, UUID> {

    List<OpcaoEnquete> findByEnqueteIdOrderByOrdemAsc(UUID enqueteId);
    
    @Query("SELECT o FROM OpcaoEnquete o WHERE o.enquete.id = :enqueteId AND o.texto LIKE %:texto% ORDER BY o.ordem ASC")
    List<OpcaoEnquete> findByEnqueteIdAndTextoContaining(@Param("enqueteId") UUID enqueteId, @Param("texto") String texto);
    
    @Query("SELECT o.id, COUNT(v) as totalVotos FROM OpcaoEnquete o " +
           "LEFT JOIN o.enquete.votos v WHERE v.opcao.id = o.id AND o.enquete.id = :enqueteId " +
           "GROUP BY o.id, o.texto, o.ordem ORDER BY o.ordem ASC")
    List<Object[]> findOpcoesComVotosPorEnquete(@Param("enqueteId") UUID enqueteId);
}