package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, UUID> {
    List<Documento> findByFuncionarioId(UUID funcionarioId);
    List<Documento> findByFuncionarioIdAndTipoDocumento(UUID funcionarioId, Documento.TipoDocumento tipoDocumento);
    
    @Query("SELECT d FROM Documento d WHERE d.dataValidade BETWEEN :hoje AND :limite AND d.status != 'VENCIDO'")
    List<Documento> findDocumentosAVencer(LocalDate hoje, LocalDate limite);
    
    @Query("SELECT d FROM Documento d WHERE d.dataValidade < :hoje AND d.status != 'VENCIDO'")
    List<Documento> findDocumentosVencidos(LocalDate hoje);
    
    List<Documento> findByStatus(Documento.StatusDocumento status);
}
