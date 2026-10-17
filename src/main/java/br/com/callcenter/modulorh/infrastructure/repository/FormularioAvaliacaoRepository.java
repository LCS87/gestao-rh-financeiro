package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.FormularioAvaliacao;
import br.com.callcenter.modulorh.domain.enums.TipoFormularioAvaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormularioAvaliacaoRepository extends JpaRepository<FormularioAvaliacao, UUID> {

    List<FormularioAvaliacao> findByAtivoTrue();

    Page<FormularioAvaliacao> findByAtivoTrue(Pageable pageable);

    List<FormularioAvaliacao> findByTipoAndAtivoTrue(TipoFormularioAvaliacao tipo);

    @Query("SELECT f FROM FormularioAvaliacao f WHERE f.ativo = true " +
           "AND (f.nome ILIKE %:termo% OR f.descricao ILIKE %:termo%)")
    List<FormularioAvaliacao> searchByTermo(@Param("termo") String termo);

    @Query("SELECT f FROM FormularioAvaliacao f WHERE f.ativo = true " +
           "AND f.formularioBase.id = :formularioBaseId " +
           "ORDER BY f.versao DESC")
    List<FormularioAvaliacao> findVersoesDoFormulario(@Param("formularioBaseId") UUID formularioBaseId);

    @Query("SELECT f FROM FormularioAvaliacao f WHERE f.ativo = true " +
           "AND f.tipo = :tipo " +
           "AND f.versao = (SELECT MAX(f2.versao) FROM FormularioAvaliacao f2 WHERE f2.formularioBase.id = f.id OR f2.id = f.id)")
    Optional<FormularioAvaliacao> findUltimaVersaoAtiva(@Param("tipo") TipoFormularioAvaliacao tipo);

    @Query("SELECT COUNT(f) FROM FormularioAvaliacao f WHERE f.ativo = true")
    Long countFormulariosAtivos();

    boolean existsByNomeAndTipo(String nome, TipoFormularioAvaliacao tipo);
}