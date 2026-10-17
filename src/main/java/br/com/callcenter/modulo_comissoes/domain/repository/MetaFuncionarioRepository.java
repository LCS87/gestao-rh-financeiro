package br.com.callcenter.modulo_comissoes.domain.repository;

import br.com.callcenter.modulo_comissoes.domain.entity.MetaFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MetaFuncionarioRepository extends JpaRepository<MetaFuncionario, UUID> {

    List<MetaFuncionario> findByMetaId(UUID metaId);

    List<MetaFuncionario> findByFuncionarioId(UUID funcionarioId);

    List<MetaFuncionario> findByMetaIdAndFuncionarioId(UUID metaId, UUID funcionarioId);

    List<MetaFuncionario> findByStatus(String status);
}
