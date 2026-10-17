package br.com.callcenter.modulo_comissoes.domain.repository;

import br.com.callcenter.modulo_comissoes.domain.entity.ComissaoPaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComissaoPagaRepository extends JpaRepository<ComissaoPaga, UUID> {

    List<ComissaoPaga> findByFuncionarioId(UUID funcionarioId);

    List<ComissaoPaga> findByMesReferencia(String mesReferencia);

    List<ComissaoPaga> findByFuncionarioIdAndMesReferencia(UUID funcionarioId, String mesReferencia);
}
