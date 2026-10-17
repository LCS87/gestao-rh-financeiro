package br.com.callcenter.modulorh.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.callcenter.modulorh.domain.entity.CurtidaPostagem;
import br.com.callcenter.modulorh.domain.enums.TipoReacao;

@Repository
public interface CurtidaPostagemRepository extends JpaRepository<CurtidaPostagem, UUID> {

    Optional<CurtidaPostagem> findByPostagemIdAndFuncionarioId(UUID postagemId, UUID funcionarioId);

    List<CurtidaPostagem> findByPostagemId(UUID postagemId);

    List<CurtidaPostagem> findByFuncionarioId(UUID funcionarioId);

    List<CurtidaPostagem> findByPostagemIdAndTipoReacao(UUID postagemId, TipoReacao tipoReacao);

    Long countByPostagemId(UUID postagemId);

    Long countByPostagemIdAndTipoReacao(UUID postagemId, TipoReacao tipoReacao);

    @Query("SELECT c.postagem.id, COUNT(c), c.tipoReacao FROM CurtidaPostagem c GROUP BY c.postagem.id, c.tipoReacao ORDER BY COUNT(c) DESC")
    List<Object[]> findReacoesAgrupadasPorPostagem();

    @Query("SELECT cp.tipoReacao, COUNT(cp) FROM CurtidaPostagem cp WHERE cp.postagem.id = :postagemId GROUP BY cp.tipoReacao")
    List<Object[]> findDistribuicaoReacoesPorPostagem(@Param("postagemId") UUID postagemId);

    @Query("SELECT c.postagem.id, COUNT(c) as totalCurtidas FROM CurtidaPostagem c GROUP BY c.postagem.id ORDER BY totalCurtidas DESC")
    List<Object[]> findPostagensMaisCurtidas(Pageable pageable);
}