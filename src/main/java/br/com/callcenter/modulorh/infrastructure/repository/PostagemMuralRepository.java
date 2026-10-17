package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.PostagemMural;
import br.com.callcenter.modulorh.domain.enums.TipoPostagem;
import br.com.callcenter.modulorh.domain.enums.VisibilidadePostagem;
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
public interface PostagemMuralRepository extends JpaRepository<PostagemMural, UUID> {

    Page<PostagemMural> findByAtivaTrueOrderByDataPublicacaoDesc(Pageable pageable);
    
    Page<PostagemMural> findByAtivaTrueAndFixadaTrueOrderByDataPublicacaoDesc(Pageable pageable);
    
    Page<PostagemMural> findByAutorIdAndAtivaTrueOrderByDataPublicacaoDesc(UUID autorId, Pageable pageable);
    
    List<PostagemMural> findByTipoAndAtivaTrueOrderByDataPublicacaoDesc(TipoPostagem tipo);
    
    List<PostagemMural> findByVisibilidadeAndAtivaTrueOrderByDataPublicacaoDesc(VisibilidadePostagem visibilidade);
    
    @Query("SELECT p FROM PostagemMural p WHERE p.ativa = true AND p.dataPublicacao >= :dataInicio ORDER BY p.dataPublicacao DESC")
    List<PostagemMural> findPostagensRecentes(@Param("dataInicio") LocalDateTime dataInicio);
    
    @Query("SELECT p FROM PostagemMural p WHERE p.ativa = true AND " +
           "(LOWER(p.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.conteudo) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
           "ORDER BY p.dataPublicacao DESC")
    Page<PostagemMural> searchByTermo(@Param("termo") String termo, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM PostagemMural p WHERE p.ativa = true")
    Long countPostagensAtivas();
    
    @Query("SELECT p.tipo, COUNT(p) FROM PostagemMural p WHERE p.ativa = true GROUP BY p.tipo")
    List<Object[]> countPostagensPorTipo();
    
    @Query("SELECT p.autor.id, COUNT(p) FROM PostagemMural p WHERE p.ativa = true GROUP BY p.autor.id ORDER BY COUNT(p) DESC")
    List<Object[]> findTopAutores(Pageable pageable);
}