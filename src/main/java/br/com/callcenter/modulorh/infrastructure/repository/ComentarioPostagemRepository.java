package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.ComentarioPostagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComentarioPostagemRepository extends JpaRepository<ComentarioPostagem, UUID> {

    List<ComentarioPostagem> findByPostagemIdAndAtivoTrueOrderByDataComentarioDesc(UUID postagemId);
    
    List<ComentarioPostagem> findByPostagemIdAndComentarioPaiIsNullAndAtivoTrueOrderByDataComentarioDesc(UUID postagemId);
    
    List<ComentarioPostagem> findByComentarioPaiIdAndAtivoTrueOrderByDataComentarioDesc(UUID comentarioPaiId);
    
    Page<ComentarioPostagem> findByAutorIdAndAtivoTrueOrderByDataComentarioDesc(UUID autorId, Pageable pageable);
    
    @Query("SELECT c FROM ComentarioPostagem c WHERE c.postagem.id = :postagemId AND c.ativo = true " +
           "ORDER BY CASE WHEN c.comentarioPai IS NULL THEN 0 ELSE 1 END, c.dataComentario DESC")
    List<ComentarioPostagem> findComentariosHierarquicosPorPostagem(@Param("postagemId") UUID postagemId);
    
    @Query("SELECT COUNT(c) FROM ComentarioPostagem c WHERE c.postagem.id = :postagemId AND c.ativo = true")
    Long countComentariosPorPostagem(@Param("postagemId") UUID postagemId);
    
    @Query("SELECT c.postagem.id, COUNT(c) FROM ComentarioPostagem c WHERE c.ativo = true GROUP BY c.postagem.id ORDER BY COUNT(c) DESC")
    List<Object[]> findPostagensMaisComentadas(Pageable pageable);
}